package com.example.itime3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.itime3.data.FileDataSource;
import com.example.itime3.data.Schedule;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final int CONTEXT_MENU_DELETE = 1;
    public static final int CONTEXT_MENU_UPDATE = CONTEXT_MENU_DELETE + 1;
    public static final int REQUEST_CODE_UPDATE_SCHEDULE = 902;
    public static final int REQUEST_CODE_NEW_SCHEDULE = 901;
    private ListView listViewSchedules;
    private ArrayList<Schedule> listSchedules = new ArrayList<>();
    private ScheduleAdapter scheduleAdapter;
    private FileDataSource fileDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitData();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditScheduleActivity.class);
                startActivityForResult(intent, REQUEST_CODE_NEW_SCHEDULE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        listViewSchedules = (ListView) this.findViewById(R.id.listview);
        scheduleAdapter = new ScheduleAdapter(MainActivity.this, R.layout.list_view_schedule, listSchedules);
        listViewSchedules.setAdapter(scheduleAdapter);
        this.registerForContextMenu(listViewSchedules);
        listViewSchedules.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Schedule schedule = listSchedules.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("photo",schedule.getCoverResourceId());
                bundle.putString("message1",schedule.getTitle());
                bundle.putString("message2",schedule.getDeadline());
                bundle.putString("message3",schedule.getRemark());
                Intent intent = new Intent();
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, Countdown.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileDataSource.save();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        if (v == findViewById(R.id.listview)) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            //设置标题
            menu.setHeaderTitle(listSchedules.get(info.position).getTitle());
            //添加内容
            menu.add(0, CONTEXT_MENU_DELETE, 0, "删除");
            menu.add(0, CONTEXT_MENU_UPDATE, 0, "更新");
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case CONTEXT_MENU_UPDATE: {
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Schedule schedule = listSchedules.get(menuInfo.position);

                Intent intent = new Intent(MainActivity.this, EditScheduleActivity.class);
                intent.putExtra("edit_position", menuInfo.position);
                intent.putExtra("schedule_title", schedule.getTitle());
                intent.putExtra("schedule_remark", schedule.getRemark());
                //startActivityForResult()方法是主活动MainActivity用来启动EditScheduleActivity的
                startActivityForResult(intent, REQUEST_CODE_UPDATE_SCHEDULE);
                break;
            }
            case CONTEXT_MENU_DELETE: {
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                final int itemPosition = menuInfo.position;
                new android.app.AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("询问")
                        .setMessage("你确定要删除这条吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                listSchedules.remove(itemPosition);
                                scheduleAdapter.notifyDataSetChanged();
                                Toast.makeText(MainActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .create().show();
                break;
            }

        }
        return super.onContextItemSelected(item);
    }

    @Override
    //onActivityResult（）是从NewBookActiviy回调到BookListActivity的
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_NEW_SCHEDULE:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra("edit_position", 0);
                    String title = data.getStringExtra("schedule_title");
                    String remark = data.getStringExtra("schedule_remark");
                    String deadline = data.getStringExtra("schedule_date");
                    getListSchedules().add(position, new Schedule(title, R.drawable.windwill, remark, deadline));
                    //通知适配器已改变
                    scheduleAdapter.notifyDataSetChanged();

                    Toast.makeText(this, "新建成功", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_UPDATE_SCHEDULE:
                if (resultCode == RESULT_OK) {
                    int position = data.getIntExtra("edit_position", 0);
                    String title = data.getStringExtra("schedule_title");
                    String remark = data.getStringExtra("schedule_remark");
                    String deadline = data.getStringExtra("schedule_date");
                    Schedule schedule = getListSchedules().get(position);

                    schedule.setTitle(title);
                    schedule.setRemark(remark);
                    schedule.setDeadline(deadline);
                    //通知适配器已改变
                    scheduleAdapter.notifyDataSetChanged();

                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public List<Schedule> getListSchedules() {
        return listSchedules;
    }

    private void InitData() {
        fileDataSource = new FileDataSource(this);
        listSchedules = fileDataSource.load();
        if (listSchedules.size() == 0)
            listSchedules.add(new Schedule("标题", R.drawable.windwill, "备注", "截至日期"));

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_moment) {
            // Handle the camera action
        } else if (id == R.id.nav_sign) {

        } else if (id == R.id.nav_widget) {

        } else if (id == R.id.nav_color) {

        } else if (id == R.id.nav_prime) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class ScheduleAdapter extends ArrayAdapter<Schedule> {

        private int resourceId;

        public ScheduleAdapter(Context context, int resource, List<Schedule> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater mInflater = LayoutInflater.from(this.getContext());
            View item = mInflater.inflate(this.resourceId, null);

            ImageView scheduleImage = item.findViewById(R.id.image_view_schedule);
            TextView scheduleTitle = item.findViewById(R.id.text_view_schedule);

            Schedule schedule_item = this.getItem(position);
            scheduleImage.setImageResource(schedule_item.getCoverResourceId());
            scheduleTitle.setText(schedule_item.getTitle() + "\n" + schedule_item.getDeadline() + "\n" + schedule_item.getRemark());

            return item;
        }
    }
}

