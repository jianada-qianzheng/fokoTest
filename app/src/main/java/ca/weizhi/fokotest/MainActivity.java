package ca.weizhi.fokotest;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {


    private DrawerLayout drawerLayout;

    private ListView teamListView;

    private ArrayList<Team> teams;

    private ArrayList<Player> players;

    private ArrayList<Player> leftWingPlayers;

    private ArrayList<Player> rightWingPlayers;

    private ArrayList<Player> centerPlayers;

    private ArrayList<Player> denfensePlayers;

    private ArrayList<Player> goliePlayers;

    private String TAG = "MainActivity";

    private ListView playerListView;

    private TextView playerIdSortView;

    private TextView playerNameSortView;

    private TextView playerPositionFilterView;

    private PlayerListAdapter playerListAdapter;

    private boolean sortIdAsc;

    private boolean sortNameAsc;

    private String sortType;

    private boolean includeLeftWing;

    private boolean includeRightWing;

    private boolean includeCenter;

    private boolean includeDefense;

    private boolean includeGolie;

    private String teamUrl;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

        initValue();


        // in the background, read the string of teams information from team url
        // after getting the string, load the team list view
        new readTeamJsonTask().execute(teamUrl);



        // after click the playerIdSortView clicked
        // sort the players list by ID
        playerIdSortView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sortById();

            }
        });

        //after click the playerNameSortView clicked
        // sort the players list by name
        playerNameSortView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sortByName();

            }
        });

        // after click the playerPositionFilterView
        // pop up a window which contains 5 checkboxes
        // when rightWingCheckBox checked, the players list would include the rightWingPlayers list
        // when rightwingCheckBox unchecked, the players list would reomve all the rightWing players
        playerPositionFilterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initPopWindow(playerPositionFilterView);

            }
        });

    }





    private void initUI(){

        playerIdSortView=findViewById(R.id.player_id_sort_view);

        playerNameSortView=findViewById(R.id.player_name_sort_view);

        playerPositionFilterView=findViewById(R.id.player_position_filter_view);

        drawerLayout =  findViewById(R.id.drawer_layout);

        teamListView =  findViewById(R.id.team_list_view);

        playerListView = findViewById(R.id.player_list_view_1);

        Toolbar toolbar;

        toolbar =  findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.app_name);

        getSupportActionBar().hide();

        drawerLayout =  findViewById(R.id.drawer_layout);

        android.support.v7.app.ActionBarDrawerToggle drawerToggle =
                new android.support.v7.app.ActionBarDrawerToggle
                        (this,drawerLayout,toolbar,R.string.app_name, R.string.app_name);

        drawerToggle.syncState();
    }

    private void initValue(){
        players=new ArrayList<>();

        leftWingPlayers=new ArrayList<>();

        rightWingPlayers=new ArrayList<>();

        centerPlayers=new ArrayList<>();

        denfensePlayers=new ArrayList<>();

        goliePlayers=new ArrayList<>();

        teams = new ArrayList<>();

        sortType= "sortById";

        sortIdAsc=true;

        sortNameAsc=true;

        includeLeftWing=true;

        includeRightWing=true;

        includeCenter=true;

        includeDefense=true;

        includeGolie=true;

        teamUrl = "https://statsapi.web.nhl.com/api/v1/teams";
    }





    // in the background, read the string of teams information from team url
    // after getting the string, load the team list view
    private class readTeamJsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {

            super.onPreExecute();

        }


        // read the string of team information in the background
        protected String doInBackground(String... params) {

            Log.i(TAG,"do in background");
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();

                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {

                    buffer.append(line+"\n");

                    Log.d("Response: ", "> " + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            } finally {
                if (connection != null) {

                    connection.disconnect();

                }
                try {

                    if (reader != null) {

                        reader.close();

                    }
                } catch (IOException e) {

                    e.printStackTrace();

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //after getting the string from url
            //load the team list
            loadTeamList(result);

            Log.i(TAG,"team list count"+teams.size());

            TeamListAdapter adapter =
                    new TeamListAdapter(MainActivity.this,
                            R.layout.team_list_item_row, teams);

            teamListView.setAdapter(adapter);


            // when click one item of team list view
            // do the selectTeam function
            teamListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {

                    selectTeam(position);


                }
            });
        }
    }

    //load the teams list
    private void loadTeamList(String teamsJsonString){


        Log.i(TAG,"read list begin");

        if(teamsJsonString!=null){

            Log.i("team json String",teamsJsonString);

            try {

                JSONObject rawObj = new JSONObject(teamsJsonString);

                JSONArray teamsJsonArray= rawObj.getJSONArray("teams");

                Log.i(TAG,"teamsJsonarry count:"+teamsJsonArray.length());

                teams.clear();

                for (int i=0;i<teamsJsonArray.length();i++){

                    JSONObject teamJsonObject =  teamsJsonArray.getJSONObject(i);

                    int teamId = teamJsonObject.getInt("id");

                    String teamName = teamJsonObject.getString("name");

                    teams.add(new Team(teamId,teamName));
                }

            } catch (Throwable t) {

                Log.e(TAG, "Could not parse malformed JSON: teamString");

            }

        }


    }



    // after click one item of the itme in the team list
    // get the team id and generate a specific url with team id
    // start another async task to read the string of players information in the team
    // after getting the string, load the player list
    private void selectTeam(int position) {

        playerIdSortView.setVisibility(View.VISIBLE);

        playerNameSortView.setVisibility(View.VISIBLE);

        playerPositionFilterView.setVisibility(View.VISIBLE);

        players=new ArrayList<>();

        int teamId=teams.get(position).getId();

        String playerUrl="https://statsapi.web.nhl.com/api/v1/teams/"+teamId+"/roster";

        new readPlayerJsonTask().execute(playerUrl);

        playerListView=findViewById(R.id.player_list_view_1);

        teamListView.setItemChecked(position, true);

        teamListView.setSelection(position);

        drawerLayout.closeDrawer(teamListView);

    }


    // read the string of players information in the background
    // after gettin the string, load the players list
    private class readPlayerJsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {

            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            Log.i(TAG,"do in background");

            HttpURLConnection connection = null;

            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();

                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";

                while ((line = reader.readLine()) != null) {

                    buffer.append(line+"\n");

                    Log.d("Response: ", "> " + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            } finally {

                if (connection != null) {

                    connection.disconnect();

                }
                try {

                    if (reader != null) {

                        reader.close();

                    }
                } catch (IOException e) {

                    e.printStackTrace();

                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //after getting the result, load the players list
            loadPlayerList(result);

            // in the playersListAdapter, set a on click listener
            // which will generate an Intent to PlayerCountryActivity
            playerListAdapter=new PlayerListAdapter(MainActivity.this,players);

            playerListView.setAdapter(playerListAdapter);


        }
    }

    // load the players list
    // divide the players list into 5 groups:
    // leftWingPlayers,rightWingPlayers,centerPlayers,goaliePlayers,defensePlayers
    // players will include some of the 5 groups above according to the filter
    private void loadPlayerList(String playersJsonString){


        Log.i(TAG,"read list begin");

        if(playersJsonString!=null){

            Log.i("player json String",playersJsonString);

            try {

                JSONObject rawObj = new JSONObject(playersJsonString);

                JSONArray playerJsonArray= rawObj.getJSONArray("roster");

                Log.i(TAG,"playersJsonArry count:"+playerJsonArray.length());

                players.clear();

                leftWingPlayers.clear();

                rightWingPlayers.clear();

                centerPlayers.clear();

                goliePlayers.clear();

                denfensePlayers.clear();

                for (int i=0;i<playerJsonArray.length();i++){

                    JSONObject playerJsonObject =  playerJsonArray.getJSONObject(i);

                    JSONObject personJsonObject = playerJsonObject.getJSONObject("person");

                    JSONObject positionJsonObject = playerJsonObject.getJSONObject("position");



                    int playerId = personJsonObject.getInt("id");

                    String playerName = personJsonObject.getString("fullName");

                    String positionCode = positionJsonObject.getString("code");

                    String position = positionJsonObject.getString("name");




                    if(positionCode.equals("L")){

                        leftWingPlayers.add(new Player(playerId,playerName,position));

                    }else if(positionCode.equals("R")){

                        rightWingPlayers.add(new Player(playerId,playerName,position));

                    }else if(positionCode.equals("C")){

                        centerPlayers.add(new Player(playerId,playerName,position));

                    }else if(positionCode.equals("D")){

                        denfensePlayers.add(new Player(playerId,playerName,position));

                    }else if(positionCode.equals("G")){

                        goliePlayers.add(new Player(playerId,playerName,position));

                    }

                }

                players.addAll(leftWingPlayers);
                players.addAll(rightWingPlayers);
                players.addAll(centerPlayers);
                players.addAll(denfensePlayers);
                players.addAll(goliePlayers);

                sortPlayers();

            } catch (Throwable t) {

                Log.e(TAG, t.toString());

            }

        }


    }

    // sort the players list by id
    private void sortById(){

        sortType = "sortById";

        playerNameSortView.setText("Name");

        if(sortIdAsc){
            sortIdAsc=false;

            playerIdSortView.setText("ID  ↓");
        }else{
            sortIdAsc=true;

            playerIdSortView.setText("ID  ↑");
        }
        Comparator comparator = new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {

                if(sortIdAsc){
                    if(o1.getId()>o2.getId())
                        return 1;
                    else
                        return -1;
                }else{
                    if(o1.getId()>o2.getId())
                        return -1;
                    else
                        return 1;
                }

            }
        };

        Collections.sort(players,comparator);

        playerListAdapter.notifyDataSetChanged();



    }

    //sort players list by name
    private void sortByName(){

        sortType="sortByName";

        playerIdSortView.setText("ID");

        if(sortNameAsc){

            sortNameAsc=false;

            playerNameSortView.setText("Name  ↓");

        }else{

            sortNameAsc=true;

            playerNameSortView.setText("Name  ↑");

        }
        Comparator comparator = new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {

                if(sortNameAsc){

                    if(o1.getName().compareTo(o2.getName())>0)

                        return 1;

                    else

                        return -1;
                }else{

                    if(o1.getName().compareTo(o2.getName())>0)

                        return -1;

                    else

                        return 1;
                }

            }
        };

        Collections.sort(players,comparator);

        playerListAdapter.notifyDataSetChanged();



    }

    // pop up a window which contains 5 checkboxes
    // when rightWingCheckBox checked, the players list would include the rightWingPlayers list
    // when rightwingCheckBox unchecked, the players list would reomve all the rightWing players
    private void initPopWindow(View v) {
        View view =
                LayoutInflater.from(this).inflate
                        (R.layout.filter_pop_up, null, false);


        CheckBox rightWingCheckBox = view.findViewById(R.id.right_wing) ;

        CheckBox leftWingCheckBox = view.findViewById(R.id.left_wing);

        CheckBox denfensemanCheckBox = view.findViewById(R.id.defenseman);

        CheckBox centerCheckBox = view.findViewById(R.id.center);

        CheckBox goalieCheckBox = view.findViewById(R.id.goalie);


        centerCheckBox.setChecked(includeCenter);

        leftWingCheckBox.setChecked(includeLeftWing);

        rightWingCheckBox.setChecked(includeRightWing);

        denfensemanCheckBox.setChecked(includeDefense);

        goalieCheckBox.setChecked(includeGolie);


        final PopupWindow popWindow = new PopupWindow
                (view, ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, true);



        popWindow.setTouchable(true);

        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;

            }
        });

        popWindow.setBackgroundDrawable(new ColorDrawable(0xffffffff));

        popWindow.showAsDropDown(v, 50, 0);

        rightWingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                includeRightWing=isChecked;

                // when rightWingCheckBox checked, the players list would include
                // the rightWingPlayers list
                // when rightwingCheckBox unchecked, the players list would reomve
                // all the rightWing players
                if (isChecked){

                    includeRightWing=true;

                    players.addAll(rightWingPlayers);

                }else {

                    players.removeAll(rightWingPlayers);

                }

                sortPlayers();

            }
        });

        leftWingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                includeLeftWing=isChecked;

                if (isChecked){
                    players.addAll(leftWingPlayers);




                }else {
                    Log.i(TAG,"reomve leftwing:"+leftWingPlayers.size());

                    Log.i(TAG,"player count before:"+players.size());


                    players.removeAll(leftWingPlayers);

                    Log.i(TAG,"player count after remove:"+players.size());

                }

                sortPlayers();


            }
        });

        centerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                includeCenter=isChecked;
                if (isChecked){
                    players.addAll(centerPlayers);




                }else {
                    Log.i(TAG,"reomve center:"+centerPlayers.size());

                    players.removeAll(centerPlayers);
                }

                sortPlayers();

            }
        });

        denfensemanCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                includeDefense=isChecked;

                if (isChecked){

                    players.addAll(denfensePlayers);

                }else {

                    Log.i(TAG,"reomve denfense:"+denfensePlayers.size());

                    players.removeAll(denfensePlayers);
                }

                sortPlayers();

            }
        });

        goalieCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                includeGolie=isChecked;

                if (isChecked){

                    players.addAll(goliePlayers);

                }else {

                    Log.i(TAG,"reomve golie:"+goliePlayers.size());

                    players.removeAll(goliePlayers);
                }

                sortPlayers();

            }
        });


    }

    //sort the players list by the sort type
    public void sortPlayers(){
        if(sortType.equals("sortByName")){

            // when do the sortPlayers function,
            // will change the desc to asc or
            // change the asc to desc
            if(sortNameAsc){

                sortNameAsc=false;

                sortByName();

            }else{

                sortNameAsc=true;

                sortByName();
            }

        }else{

            if(sortIdAsc){

                sortIdAsc=false;

                sortById();
            }else{
                sortIdAsc=true;

                sortById();
            }

        }

        playerListAdapter.notifyDataSetChanged();
    }
}