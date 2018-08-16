package sg.edu.rp.webservices.dmsdchatapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.text.format.DateFormat;

public class CustomMessage extends ArrayAdapter<Message>{
    Context context;
    ArrayList<Message> messages;
    int resource;
    TextView tvUser, tvDateTime, tvMessage;

    public CustomMessage(Context context, int resource, ArrayList<Message> messages) {
        super(context, resource, messages);
        this.context = context;
        this.messages = messages;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("getView", "show" + position);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.message_row, parent, false);

        //Match the UI components with Java variables
        tvUser = rowView.findViewById(R.id.tvUser);
        tvDateTime = rowView.findViewById(R.id.tvDateTime);
        tvMessage = rowView.findViewById(R.id.tvMessage);

        Message contact = messages.get(position);

        tvUser.setText(contact.getMessageUser());
        String time = String.valueOf(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", contact.getMessageTime()));
        tvDateTime.setText(time);
        tvMessage.setText(String.valueOf(contact.getMessageText()));

        return rowView;
    }
}
