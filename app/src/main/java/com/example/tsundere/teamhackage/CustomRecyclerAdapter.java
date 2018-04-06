package com.example.tsundere.teamhackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

//Адаптер для RecyclerView находящегося в MemberListActivity

public class CustomRecyclerAdapter
        extends RecyclerView.Adapter<CustomRecyclerAdapter.MemberViewHolder> {

    private List<Integer> membersList; //Список идентификаторов участников

    public CustomRecyclerAdapter(List<Integer> membersList) {
        this.membersList = membersList;
    }

    public class MemberViewHolder extends RecyclerView.ViewHolder
                                                implements View.OnClickListener{
        TextView name;
        TextView group;
        ImageView photo;
        private Context context;

        public MemberViewHolder(View itemView) {
            super(itemView);
            //сохраняем поля из CardView в ViewHolder
            name = (TextView)itemView.findViewById(R.id.member_name);
            group = itemView.findViewById(R.id.member_group);
            context = itemView.getContext();
            photo = (ImageView)itemView.findViewById(R.id.member_photo);
            itemView.setOnClickListener(this); //Делаем карточки кликабельными
        }

        @Override
        public void onClick(View view) {
            //Формируем Intent для перехода к активити, отображающей подробную информацию об участнике
            Intent memberInfoIntent = new Intent(context, MemberInfoActivity.class);
            memberInfoIntent.putExtra("member_id",membersList.get(getLayoutPosition()));
            context.startActivity(memberInfoIntent);
        }
    }


    @Override
    public MemberViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_layout, parent, false);
        return new MemberViewHolder(v);
    }

    //Устанавливаем отображение карточки на экране
    @Override
    public void onBindViewHolder(MemberViewHolder holder, int position) {
        ReadableDBHelper database = new ReadableDBHelper(holder.context, membersList.get(position)); //Получаем доступ к базе данных для чтения

        String Name = database.getMemberFirstName() +
                " " + database.getMemberSecondName();

        //Устанавливаем значения полям карточки
        holder.name.setText(Name);
        holder.group.setText(database.getMemberGroup());
        byte[] arr = database.getImageArray();
        Bitmap img = ImageDecoder.bitmapFromByteArrayLowQ(arr); //Преобразовать байтовый массив в сжатый Bitmap
        holder.photo.setImageBitmap(img);
        database.close();
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
