package com.colonelfund.colonelfund;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Event list view class.
 */
public class EventListActivity extends AppCompatActivity {
    private ListView lv = null;
    private ArrayAdapter arrayAdapter =  null;
    private EditText searchBar = null;
    Button showMenu;
    /**
     * Overrides on create in order to draw event list and sets listeners for buttons and search.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_event_list);
        searchBar = (EditText) findViewById(R.id.editText);
        lv = (ListView) findViewById(R.id.eventListView);
        final EventCollection ecf = new EventCollection(getApplicationContext());
        Collection<Event> eventList = ecf.getEventsList();
        //make array adapter
        arrayAdapter = new EventListAdapter(this, generateData(eventList));
        lv.setAdapter(arrayAdapter);
        // set listener for each item
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                EventListModel item = (EventListModel) lv.getItemAtPosition(position);
                String myItem = item.getTitle();
                Intent intent = new Intent(EventListActivity.this, ViewEventActivity.class);
                intent.putExtra("SelectedEvent", ecf.get(myItem));
                startActivity(intent);
            }
        });
        lv.setTextFilterEnabled(true);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Text ["+s+"]");

                arrayAdapter.getFilter().filter(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        /**Testing Dropdown Menu Start
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout)inflater.inflate(R.layout.pop_up_window, (ViewGroup)findViewById(R.id.PopUpView));
        PopupWindow  pw = new PopupWindow(layout, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setTouchable(true);
        //Testing Dropdown Menu End */
    }
    /**
     * Inflates the main menu bar.
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    /**
     * Gets the information on buttons selected and takes the appropriate action.
     * @param item
     * @return selectedItem
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.about_you) {
            Intent intent = new Intent(this, ViewProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.your_history_events) {
            Intent intent = new Intent(this, MyHistoryEventsActivity.class);
            startActivity(intent);
        } else if (id == R.id.logout_item) {
            AccessToken token = AccessToken.getCurrentAccessToken();
            if(token != null) {
                LoginManager.getInstance().logOut();
            }
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Generates Initials and User Name for Event List.
     *
     * @param eventList
     * @return eventModel
     */
    private ArrayList<EventListModel> generateData(Collection eventList) {
        ArrayList<EventListModel> models = new ArrayList<EventListModel>();
        Iterator<Event> EventItr = eventList.iterator();
        while (EventItr.hasNext()) {
            Event temp = EventItr.next();
            double goalProgress;
            if ((temp.getCurrentFunds()/temp.getFundGoal()) < 1) {
                goalProgress = (temp.getCurrentFunds()/temp.getFundGoal());
            } else {
                goalProgress = 1;
            }
            models.add(new EventListModel(temp.getTitle(),temp.getType(),temp.getAssociatedMember(),
                    temp.getEventDate(),goalProgress,temp.getDescription()));
        }
        return models;
    }
}
/**
 * Filterable Event list adapter class. Allows the array to be search by custom variables.
 */
class EventListAdapter extends ArrayAdapter<EventListModel> implements Filterable {
    private final Context context;
    private ArrayList<EventListModel> originalArrayList;
    private ArrayList<EventListModel> filteredModelsArrayList;
    private ItemFilter eFilter = new ItemFilter();
    private final String[] months = {"J\nA\nN",
            "F\nE\nB",
            "M\nA\nR",
            "A\nP\nR",
            "M\nA\nY",
            "J\nU\nN",
            "J\nU\nL",
            "A\nU\nG",
            "S\nE\nP",
            "O\nC\nT",
            "N\nO\nV",
            "D\nE\nC"};
    /**
     * Constructor for member list item adapter.
     * @param context
     * @param data
     */
    public EventListAdapter(Context context, ArrayList<EventListModel> data) {
        super(context, R.layout.event_list_item, data);
        this.context = context;
        this.originalArrayList = new ArrayList<EventListModel>(data);
        this.filteredModelsArrayList = new ArrayList<EventListModel>(data);
    }
    /**
     * Gets View for Event List Item.
     * @param position
     * @param convertView
     * @param parent
     * @return EventListView
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;
        holder = new ViewHolder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.event_list_item, parent, false);

            holder.eventView = inflater.inflate(R.layout.event_list_item, parent, false);
            convertView.setTag(holder);
        } else {
            holder.eventView = convertView;
        }
        // view holders for information
        TextView eventName = (TextView) holder.eventView.findViewById(R.id.memberName);
        TextView eventMember = (TextView) holder.eventView.findViewById(R.id.eventUser);
        ProgressBar goalProgress = (ProgressBar) holder.eventView.findViewById(R.id.goalProgress);
        ImageView eventType = (ImageView) holder.eventView.findViewById(R.id.member_initials);
        TextView eventDay = (TextView) holder.eventView.findViewById(R.id.event_day_box);
        TextView eventMonth = (TextView) holder.eventView.findViewById(R.id.event_month_box);
        //set main view to specific view holders
        eventName.setText(filteredModelsArrayList.get(position).getTitle());
        eventMember.setText(filteredModelsArrayList.get(position).getAssociatedMember());
        goalProgress.setProgress(filteredModelsArrayList.get(position).getGoalProgress().intValue());
        String eventDate = filteredModelsArrayList.get(position).getEventDate();
        eventDay.setText(eventDate.substring((eventDate.length()-2), (eventDate.length())));
        String monthString = eventDate.substring((eventDate.length()-5), (eventDate.length()- 3));
        int month = Integer.parseInt(monthString);
        eventMonth.setText(months[month-1]);
        if (filteredModelsArrayList.get(position).getType().equalsIgnoreCase("bbq")) {
            eventType.setImageResource(R.drawable.bbq);
        } else if (filteredModelsArrayList.get(position).getType().equalsIgnoreCase("emergency")) {
            eventType.setImageResource(R.drawable.emergency);
        } else if (filteredModelsArrayList.get(position).getType().equalsIgnoreCase("medical")) {
            eventType.setImageResource(R.drawable.medical);
        } else if (filteredModelsArrayList.get(position).getType().equalsIgnoreCase("party")) {
            eventType.setImageResource(R.drawable.party);
        } else if (filteredModelsArrayList.get(position).getType().equalsIgnoreCase("unknown")) {
            eventType.setImageResource(R.drawable.unknown);
        } else {
            eventType.setImageResource(R.drawable.question);
        }
        return holder.eventView;
    }
    /**
     * Holds eventView for filterable events
     */
    static class ViewHolder {
        View eventView;
    }
    /**
     * Gets filter for Events
     * @return eventFilter
     */
    public Filter getFilter() {
        return eFilter;
    }
    /**
     * Overrides ItemFilter class in order to filter by custom variables in an event object.
     */
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final ArrayList<EventListModel> list = new ArrayList<EventListModel>(originalArrayList);
            System.out.println("Original Array List Size: " + originalArrayList.size());
            int count = list.size();
            final ArrayList<EventListModel> nlist = new ArrayList<EventListModel>(count);
            EventListModel filterableModel;
            for (int i = 0; i < count; i++) {
                filterableModel = list.get(i);
                if (filterableModel.getTitle().toLowerCase().contains(filterString)) {
                    nlist.add(filterableModel);
                    System.out.println("Added event: " + filterableModel.getTitle());
                } else if (filterableModel.getType().toLowerCase().contains(filterString)) {
                    nlist.add(filterableModel);
                    System.out.println("Added event: " + filterableModel.getTitle());
                } else if (filterableModel.getAssociatedMember().toLowerCase().contains(filterString)) {
                    nlist.add(filterableModel);
                    System.out.println("Added event: " + filterableModel.getTitle());
                } else if (filterableModel.getEventDate().toLowerCase().contains(filterString)) {
                    nlist.add(filterableModel);
                    System.out.println("Added event: " + filterableModel.getTitle());
                } else if (filterableModel.getEventDesc().toLowerCase().contains(filterString)) {
                    nlist.add(filterableModel);
                    System.out.println("Added event: " + filterableModel.getTitle());
                }
            }
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }
        /**
         * Override filters publish results for array list
         * @param constraint
         * @param results
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredModelsArrayList = (ArrayList<EventListModel>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filteredModelsArrayList.size(); i < l; i++)
                add((EventListModel) filteredModelsArrayList.get(i));
            notifyDataSetInvalidated();
        }
        /**
         * Get the size of a filtered array list
         * @return arrayListSize
         */
        public int getCount() {
            if(filteredModelsArrayList==null){
                return 0;
            }else{
                return filteredModelsArrayList.size();
            }
        }
        /**
         * Get a filterable items position
         * @param position
         * @return itemPosition
         */
        public EventListModel getItem(int position) {
            return filteredModelsArrayList.get(position);
        }
        /**
         * Get a filterable items ID
         * @param position
         * @return itemID
         */
        public long getItemId(int position) {
            return position;
        }
    }
}
/**
 * Event list Item Model class.
 */
class EventListModel {
    private String title;
    private String type;
    private String associatedMember;
    private String eventDate;
    private Double goalProgress;
    private String eventDescription;
    /**
     * Constructor for Event List Model with 6 args
     * @param title of event
     * @param type of event
     * @param associatedMember of event
     * @param eventDate of event
     * @param goalProgress of event
     * @param eventDesc of event
     */
    public EventListModel(String title, String type, String associatedMember, String eventDate, Double goalProgress, String eventDesc) {
        super();
        this.title = title;
        this.type = type;
        this.associatedMember = associatedMember;
        this.eventDate = eventDate;
        this.goalProgress = (goalProgress*100);
        this.eventDescription = eventDesc;
    }
    /**
     * @return eventType
     */
    public String getType() {
        return type;
    }
    /**
     * @return eventTitle
     */
    public String getTitle() {
        return title;
    }
    /**
     * @return eventAssociatedMember
     */
    public String getAssociatedMember() {
        return associatedMember;
    }
    /**
     * @return eventDate
     */
    public String getEventDate() {
        return eventDate;
    }
    /**
     * @return eventGoalProgress
     */
    public Double getGoalProgress() {
        return goalProgress;
    }
    /**
     * @return eventDescription
     */
    public String getEventDesc() {
        return eventDescription;
    }
}