package com.example.socialmedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmedia.FollowRequest;

import java.util.List;

public class FollowRequestAdapter extends ArrayAdapter<FollowRequest> {

    private Context context;
    private List<FollowRequest> requests;

    public FollowRequestAdapter(Context context, List<FollowRequest> requests) {
        super(context, R.layout.follow_request_item, requests);
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.follow_request_item, null);
        }

        FollowRequest request = requests.get(position);

        TextView userIdTextView = view.findViewById(R.id.userIdTextView);
        userIdTextView.setText(request.getUserId());

        Button acceptButton = view.findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(v -> {
            request.setAccepted(true);
//            followRequestsRef.child(request.getUserId()).setValue(request);
            requests.remove(request);
            notifyDataSetChanged();
        });

        Button rejectButton = view.findViewById(R.id.rejectButton);
        rejectButton.setOnClickListener(v -> {
            requests.remove(request);
//            followRequestsRef.child(request.getUserId()).removeValue();
            notifyDataSetChanged();
        });

        return view;
    }
}
