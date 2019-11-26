package fitnesscompanion.com.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import fitnesscompanion.com.Model.Diet;
import fitnesscompanion.com.R;

/**
 * Created by Soon Kok Fung
 */

public class DietExpandableAdapter extends BaseExpandableListAdapter {


    @BindView(R.id.txtTitle) TextView txtTitle;
    @BindView(R.id.txtCal) TextView txtCal;
    @BindView(R.id.txtQty) TextView txtQty;
    @BindView(R.id.imageView) ImageView imageView;

    private ArrayList<String> listDataHeader;
    private HashMap<String, ArrayList<Diet>> listData;
    private Context context;

    public DietExpandableAdapter( Context context,ArrayList<String> listDataHeader, HashMap<String, ArrayList<Diet>> listData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listData = listData;
        notifyDataSetChanged();
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.adapter_diet_header,null);

        ((TextView)convertView.findViewById(R.id.txtTitle)).setText((String)getGroup(groupPosition));
        ArrayList<Diet> diet = listData.get(getGroup(groupPosition));
        int cal =0;
        for(int x=0;x<diet.size();x++) {
            cal += diet.get(x).getCalories();
        }

        ((TextView)convertView.findViewById(R.id.txtCal)).
                setText(String.valueOf(cal) + " "+context.getString(R.string.cal));

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.adapter_diet,null);
        ButterKnife.bind(this,convertView);

        Diet diet = (Diet) getChild(groupPosition,childPosition);

        txtTitle.setText(diet.getName());
        txtQty.setText(String.valueOf(diet.getQty()) + " "+"Portion");
        txtCal.setText(String.valueOf(diet.getCalories())+" cal");

        /*if(diet.getImage().length()!=0)
            imageView.setImageBitmap(diet.getImageFromJSon());*/

        return convertView;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listData.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listData.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
