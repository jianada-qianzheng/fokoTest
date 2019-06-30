package ca.weizhi.fokotest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;


// this help other activity to load team list view
public class TeamListAdapter extends ArrayAdapter<Team> {

    private Context mContext;

    private int layoutResourceId;

    private ArrayList<Team> teams ;

    public TeamListAdapter(Context mContext, int layoutResourceId, ArrayList<Team> teams)
    {

        super(mContext, layoutResourceId, teams);

        this.layoutResourceId = layoutResourceId;

        this.mContext = mContext;

        this.teams = teams;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        convertView = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = convertView.findViewById(R.id.imageViewIcon);

        TextView textViewName =  convertView.findViewById(R.id.textViewName);

        Team folder = teams.get(position);

        String userAvatarUrl =
                "https://www-league.nhlstatic.com/images/logos/teams-current-primary-light/"
                        +folder.getId()+".svg";

        Utils.fetchSvg(getContext(), userAvatarUrl, imageViewIcon);

        textViewName.setText(folder.name);

        return convertView;
    }



}
