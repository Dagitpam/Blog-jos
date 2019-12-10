package com.example.blog

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.post_card.view.*

class PostViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){

fun  bindUsers(models:UserModel?) {

    var hud: KProgressHUD
    hud = KProgressHUD.create(itemView.context)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setCancellable(true)
        .setAnimationSpeed(2)

    with(models!!) {
//        hud.show()
        itemView.tv_post_title_view.text = postTitle
        itemView.tv_post_date_view.text = postDate
        itemView.tv_post_createdBy_view.text = name

        Glide.with(itemView.context)
            .load(postImage)
            .into(itemView.im_post_image_view)
        itemView.setOnClickListener(View.OnClickListener {
            val i = Intent(itemView.context,ViewPost::class.java)
            //Passing user info to view post activity
            i.putExtra("PostId",postId)
            i.putExtra("UserId",uid)
            i.putExtra("title",postTitle)
            i.putExtra("content",postContent)
            i.putExtra("posterName",name)
            i.putExtra("Date",postDate)
            i.putExtra("image",postImage)

            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            itemView.context.startActivity(i)
        })







    }
    hud.dismiss()

}


}