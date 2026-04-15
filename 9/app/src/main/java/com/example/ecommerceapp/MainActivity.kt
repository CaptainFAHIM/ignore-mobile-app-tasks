package com.example.ecommerceapp

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    data class Product(
        val id: Int,
        val name: String,
        val price: Double,
        val rating: Float,
        val category: String,
        var inCart: Boolean = false
    )

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerCart: RecyclerView
    private lateinit var tvBadge: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvEmpty: TextView

    private var isGrid = false
    private var selectedCategory = "All"

    private val products = mutableListOf<Product>()
    private val cartList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerCart = findViewById(R.id.recyclerCart)
        tvBadge = findViewById(R.id.tvBadge)
        tvTotal = findViewById(R.id.tvTotal)
        tvEmpty = findViewById(R.id.tvEmpty)

        loadProducts()
        setupCategories()
        setupRecycler()
        setupCartRecycler()
        setupSearch()
        setupButtons()
        setupItemTouch()

        onBackPressedDispatcher.addCallback(this) {
            val shop = findViewById<LinearLayout>(R.id.layoutShop)
            val cart = findViewById<LinearLayout>(R.id.layoutCart)
            if (cart.visibility == View.VISIBLE) {
                cart.visibility = View.GONE
                shop.visibility = View.VISIBLE
            } else finish()
        }
    }

    private fun loadProducts() {
        products.addAll(
            listOf(
                Product(1,"Laptop",800.0,4.5f,"Electronics"),
                Product(2,"T-Shirt",20.0,4.0f,"Clothing"),
                Product(3,"Novel Book",15.0,4.2f,"Books"),
                Product(4,"Chocolate",5.0,4.8f,"Food"),
                Product(5,"Toy Car",12.0,4.1f,"Toys"),
                Product(6,"Headphones",50.0,4.3f,"Electronics"),
                Product(7,"Jacket",60.0,4.4f,"Clothing"),
                Product(8,"Cookbook",25.0,4.6f,"Books")
            )
        )
    }

    private fun setupCategories() {
        val categories = listOf("All","Electronics","Clothing","Books","Food","Toys")
        val layout = findViewById<LinearLayout>(R.id.categoryLayout)

        categories.forEach { cat ->
            val btn = Button(this)
            btn.text = cat
            btn.setOnClickListener {
                selectedCategory = cat
                filterProducts()
            }
            layout.addView(btn)
        }
    }

    private fun setupRecycler() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ProductAdapter(products)
    }

    private fun setupCartRecycler() {
        recyclerCart.layoutManager = LinearLayoutManager(this)
        recyclerCart.adapter = ProductAdapter(cartList, true)
    }

    private fun setupSearch() {
        findViewById<SearchView>(R.id.searchView)
            .setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(q: String?) = false
                override fun onQueryTextChange(newText: String?): Boolean {
                    filterProducts(newText)
                    return true
                }
            })
    }

    private fun setupButtons() {
        findViewById<ImageButton>(R.id.btnToggle).setOnClickListener {
            isGrid = !isGrid
            recyclerView.layoutManager =
                if (isGrid) GridLayoutManager(this,2)
                else LinearLayoutManager(this)
        }

        findViewById<ImageButton>(R.id.btnCart).setOnClickListener {
            findViewById<LinearLayout>(R.id.layoutShop).visibility = View.GONE
            findViewById<LinearLayout>(R.id.layoutCart).visibility = View.VISIBLE
            updateTotal()
        }
    }

    private fun filterProducts(search: String? = "") {
        val filtered = products.filter {
            (selectedCategory=="All" || it.category==selectedCategory) &&
                    it.name.contains(search ?: "", true)
        }

        tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.adapter = ProductAdapter(filtered.toMutableList())
    }

    private fun updateBadge() {
        if (cartList.isEmpty()) {
            tvBadge.visibility = View.GONE
        } else {
            tvBadge.visibility = View.VISIBLE
            tvBadge.text = cartList.size.toString()
        }
    }

    private fun updateTotal() {
        val total = cartList.sumOf { it.price }
        tvTotal.text = "Total: $total"
    }

    private fun setupItemTouch() {
        val callback = object: ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val from = vh.adapterPosition
                val to = target.adapterPosition
                products.add(to, products.removeAt(from))
                recyclerView.adapter?.notifyItemMoved(from,to)
                return true
            }

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val pos = vh.adapterPosition
                val removed = products.removeAt(pos)
                recyclerView.adapter?.notifyItemRemoved(pos)

                Snackbar.make(recyclerView,"Deleted",Snackbar.LENGTH_LONG)
                    .setAction("UNDO"){
                        products.add(pos,removed)
                        recyclerView.adapter?.notifyItemInserted(pos)
                    }.show()
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(recyclerView)
    }

    inner class ProductAdapter(
        private val list: MutableList<Product>,
        private val isCart: Boolean = false
    ): RecyclerView.Adapter<ProductAdapter.VH>() {

        inner class VH(view: View): RecyclerView.ViewHolder(view){
            val name: TextView = view.findViewById(android.R.id.text1)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_1,parent,false)
            return VH(view)
        }

        override fun getItemCount() = list.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val product = list[position]
            holder.name.text = "${product.name} - ${product.price}"

            holder.itemView.setOnClickListener {
                if (!isCart) {
                    cartList.add(product)
                    updateBadge()
                }
            }
        }
    }
}