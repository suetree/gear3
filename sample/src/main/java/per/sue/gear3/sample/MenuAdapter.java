package per.sue.gear3.sample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import per.sue.gear3.adapter.GearRecyclerAdapter;

/**
 * Created by sure on 2018/2/8.
 */

public class MenuAdapter extends GearRecyclerAdapter<MainMenu> {
    public MenuAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindItemViewHolder(RecyclerAdapterViewHolder recyclerAdapterViewHolder, int position, MainMenu o) {
        ViewHolder viewHolder = (ViewHolder)recyclerAdapterViewHolder;
        viewHolder.menuNameTV.setText(o.name);
    }

    @Override
    public RecyclerAdapterViewHolder onCreateItemViewHolder(ViewGroup parent) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main_menu, null));
    }




    static class ViewHolder    extends RecyclerAdapterViewHolder {
        @Bind(R.id.menuNameTV)
        TextView menuNameTV;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
