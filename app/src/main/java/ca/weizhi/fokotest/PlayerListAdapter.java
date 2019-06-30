package ca.weizhi.fokotest;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

// this helps other activity load players list view
public class PlayerListAdapter extends ArrayAdapter<Player>  {

    private Context mContext;

    private ArrayList<Player> players;

    public PlayerListAdapter(Context context, ArrayList<Player> players) {

        super(context,R.layout.player_list_item_row,players);

        mContext = context;

        this.players = players;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Player getItem(int position) {
        return players.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convertView=
                LayoutInflater.from(mContext).inflate
                        (R.layout.player_list_item_row,parent,false);

        holder=new ViewHolder();

        holder.idView= (TextView) convertView.findViewById(R.id.player_id_view);

        holder.nameView= (TextView) convertView.findViewById(R.id.player_name_view);

        holder.positionView= (TextView) convertView.findViewById(R.id.player_position_view);

        convertView.setTag(holder);

        Player player = players.get(position);

        holder.idView.setText(player.getId()+"");

        holder.nameView.setText(player.getName());

        holder.positionView.setText(player.getPosition()+"");

        convertView.setClickable(true);


        // after one item of the player list view clicked,
        // would generate an Intent to PlayerCountryActivity
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("clicked"," "+players.get(position).getId());

                Intent intent =new Intent(mContext, PlayerCountryActivity.class);

                intent.putExtra("id",players.get(position).getId()+"");

                mContext.startActivity(intent);

            }
        });

        return convertView;
    }

    class ViewHolder{

        TextView nameView;

        TextView idView;

        TextView positionView;
    }
}
