package com.example.user.assignmentmap;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import android.support.v7.widget.CardView;
import android.widget.TextView;
import com.example.user.assignmentmap.R;

/**
 * Created by user on 11-04-2018.
 */

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.VersionViewHoler>
{
    //referencee of ArrayList.
    ArrayList<RestaurantDetails> restaurantDetails;

    //creating instance of context
    public Context mContext;

    //creating instance of listener ItemClicked
    private ItemClicked itemClicked;

    public RestaurantListAdapter(ArrayList<RestaurantDetails> restaurantDetails, Context mContext)
    {
        this.restaurantDetails = restaurantDetails;
        this.mContext = mContext;
    }

    @Override
    public VersionViewHoler onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //inflating item_raw Layout
       View view=LayoutInflater.from(mContext).inflate(R.layout.restaurant_card_view,parent,false);
        VersionViewHoler tempobject = new VersionViewHoler(view);

        return tempobject;
    }

    @Override
    public void onBindViewHolder(VersionViewHoler holder, int position)
    {
        holder.hotel_name.setText(restaurantDetails.get(position).getmName());
        holder.hotel_vicinity.setText(String.format("Address : %s", restaurantDetails.get(position).getmVicinity()));
        holder.hotel_menu.setText(String.format("Menu : \n%s", restaurantDetails.get(position).getMenu()));
        holder.hotel_timings.setText(String.format("Timings : \n%s", restaurantDetails.get(position).getTimings()));
        holder.hotel_contact.setText(String.format("Contact Number : %s", restaurantDetails.get(position).getContactInfo()));
    }

    @Override
    public int getItemCount()
    {
        return restaurantDetails.size();
    }

    //implementing setClickListener method on click of any item of recyclerView
    public void setClickListener(ItemClicked item)
    {
        itemClicked = item;
    }

    public class VersionViewHoler extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        CardView cardView;
        TextView hotel_name,hotel_vicinity,hotel_menu,hotel_timings,hotel_contact;

        public VersionViewHoler(View itemView)
        {
            super(itemView);

            cardView = itemView.findViewById(R.id.restaurant_card);
            hotel_name = itemView.findViewById(R.id.hotel_name);
            hotel_vicinity = itemView.findViewById(R.id.hotel_vicinity);
            hotel_menu = itemView.findViewById(R.id.hotel_menu);
            hotel_timings = itemView.findViewById(R.id.hotel_timings);
            hotel_contact = itemView.findViewById(R.id.hotel_contact_info);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if(itemClicked!=null)
            {
                itemClicked.onCLick(view,getAdapterPosition());
            }
        }

    }
}