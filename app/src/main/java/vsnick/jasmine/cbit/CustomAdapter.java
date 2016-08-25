package vsnick.jasmine.cbit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by vsnick on 25-08-2016.
 */
public class CustomAdapter extends BaseAdapter {
    Context context;
    Subject[] subjects;
    private static LayoutInflater inflater = null;
    CustomAdapter(Context context,Subject[]subjects)
    {
        this.context=context;
        this.subjects=subjects;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return subjects.length;
    }

    @Override
    public Object getItem(int position) {
        return subjects[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.sub_row, null);
        final TextView subName = (TextView) vi.findViewById(R.id.subname);
        final TextView percentage = (TextView) vi.findViewById(R.id.percentage);
        final TextView classesHeld = (TextView) vi.findViewById(R.id.classesHeld);
        final TextView classesAtten = (TextView) vi.findViewById(R.id.classesAtten);
        if(position==0)
        {
            subName.setText("Subject");
            percentage.setText("Percentage");
            classesHeld.setText("Classes Held");
            classesAtten.setText("Classes Attended");
        }
        else {
            String sub="Total";
            int i;
            if((i=subjects[position].subName.indexOf(":"))>0)
                sub= subjects[position].subName.substring(0,i);
            subName.setText(sub);
            percentage.setText(subjects[position].percentage);
            classesHeld.setText(subjects[position].classesHeld);
            classesAtten.setText(subjects[position].classesAtten);
        }
        return vi;
    }
}
