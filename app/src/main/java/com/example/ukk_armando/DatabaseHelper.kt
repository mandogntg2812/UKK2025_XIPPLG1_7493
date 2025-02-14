package com.example.ukk_armando

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DatabaseHelper(context: android.content.Context) :
    SQLiteOpenHelper(context, "KasirDB", null, 5) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, email TEXT, password TEXT, role TEXT)")
        db.execSQL("INSERT INTO users (name, email, password, role) VALUES ('Admin', 'admin@gmail.com', '123456', 'admin')")
        db.execSQL("INSERT INTO users (name, email, password, role) VALUES ('User', 'user@gmail.com', '123456', 'user')")
        db.execSQL("CREATE TABLE products (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price REAL, stock INTEGER)")
        db.execSQL(
            """
    CREATE TABLE IF NOT EXISTS keranjang (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        product_id INTEGER NOT NULL,
        product_name TEXT NOT NULL,
        price REAL NOT NULL,
        quantity INTEGER NOT NULL
    )
    """.trimIndent()
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS pembelian (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "product_id INTEGER, " +
                    "quantity INTEGER, " +
                    "total_price DOUBLE, " +
                    "date TEXT)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS barang (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "price DOUBLE, " +
                    "stock INTEGER)"
        )
        val createTableTransactions = """
    CREATE TABLE transaksi (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        produk TEXT NOT NULL,
        jumlah INTEGER NOT NULL,
        total_harga REAL NOT NULL,
        tanggal TEXT NOT NULL
    )
""".trimIndent()
        db.execSQL(createTableTransactions)

        Log.d("DatabaseHelper", "Database created successfully!")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS products")
        db.execSQL("DROP TABLE IF EXISTS keranjang") // Tambahkan ini untuk menghapus tabel yang lama
        db.execSQL("DROP TABLE IF EXISTS pembelian")
        db.execSQL("DROP TABLE IF EXISTS barang")
        db.execSQL("DROP TABLE IF EXISTS transactions")

        onCreate(db) // Panggil sekali saja agar semua tabel dibuat ulang

    }

    fun checkUser(email: String, password: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT role FROM users WHERE email=? AND password=?",
            arrayOf(email, password)
        )
        return if (cursor.moveToFirst()) {
            cursor.getString(0)
        } else {
            null
        }.also {
            cursor.close()
        }
    }

    fun registerUser(name: String, email: String, password: String, role: String): Boolean {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE email=?", arrayOf(email))
        if (cursor.count > 0) {
            cursor.close()
            return false
        }
        cursor.close()
        val stmt =
            db.compileStatement("INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)")
        stmt.bindString(1, name)
        stmt.bindString(2, email)
        stmt.bindString(3, password)
        stmt.bindString(4, role)
        stmt.executeInsert()
        return true
    }

    fun getAllUsers(): List<String> {
        val usersList = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT name, email, role FROM users", null)
        while (cursor.moveToNext()) {
            val name = cursor.getString(0)
            val email = cursor.getString(1)
            val role = cursor.getString(2)
            usersList.add("$name - $email - ($role)")
        }
        cursor.close()
        return usersList
    }

    fun addProduct(name: String, price: Double, stock: Int): Boolean {
        val db = writableDatabase
        val stmt = db.compileStatement("INSERT INTO products (name, price, stock) VALUES (?, ?, ?)")
        stmt.bindString(1, name)
        stmt.bindDouble(2, price)
        stmt.bindLong(3, stock.toLong())
        return stmt.executeInsert() != -1L
    }

    fun getAllProducts(): List<Product> {
        val productList = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT name, price, stock FROM products", null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(0)
                val price = cursor.getDouble(1)
                val stock = cursor.getInt(2)
                productList.add(Product(name, price, stock))  // Pastikan ini benar
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return productList
    }


    // Menambahkan produk ke keranjang
    fun addToCart(productId: Int, productName: String, price: Double, quantity: Int) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("product_id", productId)
        values.put("product_name", productName)
        values.put("price", price)
        values.put("quantity", quantity)
        db.insert("keranjang", null, values)
        db.close()
    }

    // Mendapatkan semua produk dalam keranjang
    fun getCartItems(): List<CartItem> {
        val cartList = mutableListOf<CartItem>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT k.id, p.name, k.price, k.quantity 
         FROM keranjang k 
        JOIN products p ON k.product_id = p.id
               """, null
        )


        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val productName = cursor.getString(1) // Ambil nama produk dari tabel products
                val price = cursor.getDouble(2)
                val quantity = cursor.getInt(3)

                cartList.add(CartItem(id, 0, productName, price, quantity)) // productId tidak perlu
            } while (cursor.moveToNext())
        }

        cursor.close()
        return cartList

    }

    fun clearCart() {
        val db = writableDatabase
        db.execSQL("DELETE FROM keranjang")
        db.close()
    }


    // Proses checkout, hapus semua item di keranjang
    fun checkoutCart() {
        val cartItems = getCartItems()
        val db = writableDatabase

        for (item in cartItems) {
            val totalPrice = item.price * item.quantity
            val values = ContentValues()
            values.put("product_id", item.productId)
            values.put("quantity", item.quantity)
            values.put("total_price", totalPrice)
            values.put("date", System.currentTimeMillis().toString())

            db.insert("pembelian", null, values)
            db.execSQL(
                "UPDATE barang SET stock = stock + ? WHERE id = ?",
                arrayOf(item.quantity, item.productId)
            )
        }

        clearCart()  // Hapus isi keranjang setelah checkout
        db.close()

    }

    fun removeCartItem(cartItemId: Int) {
        val db = writableDatabase
        db.delete("keranjang", "id=?", arrayOf(cartItemId.toString()))
        db.close()
    }

    fun getProductNameById(productId: Int): String? {
        val db = this.readableDatabase
        val query = "SELECT name FROM products WHERE id = ?"
        val cursor = db.rawQuery(query, arrayOf(productId.toString()))

        var productName: String? = null
        if (cursor.moveToFirst()) {
            productName = cursor.getString(0)
        }
        cursor.close()
        return productName
    }

    fun getAllTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val db = this.readableDatabase
        val query = "SELECT * FROM transaksi ORDER BY tanggal DESC"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val tanggal = cursor.getString(cursor.getColumnIndexOrThrow("tanggal"))
                val totalHarga = cursor.getDouble(cursor.getColumnIndexOrThrow("total_harga"))

                transactions.add(Transaction(id, tanggal, totalHarga))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return transactions
    }
}