package com.example.hp_pc.lakbayclient.historyRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.hp_pc.lakbayclient.HistorySingleActivity;
import com.example.hp_pc.lakbayclient.R;

/**
 * Created by HP-PC on 17/03/2018.
 */

public class HistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView rideID;
    public TextView time;

    public HistoryViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        rideID = itemView.findViewById(R.id.rideID);
        time = itemView.findViewById(R.id.time);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), HistorySingleActivity.class);
        Bundle b = new Bundle();
        b.putString("rideID", rideID.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);
    }
}
