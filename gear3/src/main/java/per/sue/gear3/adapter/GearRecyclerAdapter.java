package per.sue.gear3.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by sure on 2017/11/12.
 */

public abstract  class GearRecyclerAdapter<T> extends  RecyclerView.Adapter<GearRecyclerAdapter.RecyclerAdapterViewHolder> {

    protected View VIEW_FOOTER;
    protected View VIEW_HEADER;

    //Type
    protected int TYPE_NORMAL = 1000;
    protected int TYPE_HEADER = 1001;
    protected int TYPE_FOOTER = 1002;

    protected RecyclerView currRecyclerView;


    protected Context context;
    protected  OnRecyclerItemClickListener<T> onRecyclerItemClickListener;
    protected List<T> data = new ArrayList<>();



    public GearRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void addList(List<T> list){
        data.addAll(list);
        notifyDataSetChanged();
    }


    public void setList(List<T> list){
        data = list;
        notifyDataSetChanged();
    }
    @Override
    public int getItemViewType(int position) {
        if (isHeaderView(position)) {
            return TYPE_HEADER;
        } else if (isFooterView(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        try {
            if (currRecyclerView == null && currRecyclerView != recyclerView) {
                currRecyclerView = recyclerView;
            }
            ifGridLayoutManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RecyclerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return new RecyclerAdapterViewHolder(VIEW_FOOTER);
        } else if (viewType == TYPE_HEADER) {
            return new RecyclerAdapterViewHolder(VIEW_HEADER);
        } else {
            return onCreateItemViewHolder(parent);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerAdapterViewHolder viewHolder, int position) {
        if (!isHeaderView(position) && !isFooterView(position)) {
            if (haveHeaderView()) position--;
            onBindItemViewHolder(viewHolder, position, data.get(position));
        }
    }


    public abstract void  onBindItemViewHolder(RecyclerAdapterViewHolder recyclerAdapterViewHolder, int position, T t);


    public  abstract  RecyclerAdapterViewHolder onCreateItemViewHolder(ViewGroup parent);

    public void addHeaderView(View headerView) {
        if (haveHeaderView()) {
            throw new IllegalStateException("hearview has already exists!");
        } else {
            //避免出现宽度自适应
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            headerView.setLayoutParams(params);
            VIEW_HEADER = headerView;
            ifGridLayoutManager();
            notifyItemInserted(0);
        }

    }

    public void addFooterView(View footerView) {
        if (haveFooterView()) {
            throw new IllegalStateException("footerView has already exists!");
        } else {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            footerView.setLayoutParams(params);
            VIEW_FOOTER = footerView;
            ifGridLayoutManager();
            notifyItemInserted(getItemCount() - 1);
        }
    }

    private void ifGridLayoutManager() {
        if (currRecyclerView == null) return;
        final RecyclerView.LayoutManager layoutManager = currRecyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager.SpanSizeLookup originalSpanSizeLookup =
                    ((GridLayoutManager) layoutManager).getSpanSizeLookup();
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isHeaderView(position) || isFooterView(position)) ?
                            ((GridLayoutManager) layoutManager).getSpanCount() : 1;
                }
            });
        }
    }


    protected boolean haveHeaderView() {
        return VIEW_HEADER != null;
    }

    public boolean haveFooterView() {
        return VIEW_FOOTER != null;
    }

    protected boolean isHeaderView(int position) {
        return haveHeaderView() && position == 0;
    }

    protected boolean isFooterView(int position) {
        return haveFooterView() && position == getItemCount() - 1;
    }


    @Override
    public int getItemCount() {
        int count = null == data ? 0 : data.size();
        if (VIEW_FOOTER != null) {
            count++;
        }

        if (VIEW_HEADER != null) {
            count++;
        }
        return count;
    }


    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener<T> onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public static interface OnRecyclerItemClickListener<T> {
        void onItemClick(View view, T t, int position);
    }


    public static class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder{

       public RecyclerAdapterViewHolder(View itemView) {
           super(itemView);
           ButterKnife.bind(this, itemView);
       }
   }
}
