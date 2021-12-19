package com.example.cameratest.models

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cameratest.R

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*



class NewsAdapter(
    val items: Headlines,
    val context: Context,
    private val onClickListener: (View, Articles) -> Unit
) :
    RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.articles.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
    }

    fun pxToDp(px: Int): Int {
        return (px * Resources.getSystem().getDisplayMetrics().density).toInt()
    }

    // Привязывает элемент в коллекции к представлению
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.ViewTitle?.text = items.articles[position].source.name
        holder?.ViewDate?.text = items.articles[position].publishedAt
        holder?.ViewText?.text = items.articles[position].title
        val sunsetPhoto = items.articles[position].urlToImage
        val imageView = holder.imageView
        Picasso.get()
            .load(sunsetPhoto)
            // .placeholder(R.drawable.la)
            //.error(R.drawable.error)
            .fit()
            .into(imageView)
        val density = context.resources.displayMetrics.density
        //!!!надо получать размеры картинки!!! или не надо
        //imageView.getLayoutParams().width =pxToDp(items.news[position].size[0])
        //imageView.getLayoutParams().height =pxToDp(items.news[position].size[1])
        holder.itemView.setOnClickListener { view ->
            onClickListener.invoke(view, items.articles[position])
            setAlert(items.articles[position].description, items.articles[position].source.name)
        }
    }
    private fun setAlert(str: String, nameTitle: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(nameTitle)
        builder.setMessage(str)
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            //здесь можно навешать событие при закрытии
        }

        builder.show()
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val ViewTitle = view.tvID
    val ViewDate = view.tvDate
    val ViewText = view.tvSource
    val imageView = view.image_item
}