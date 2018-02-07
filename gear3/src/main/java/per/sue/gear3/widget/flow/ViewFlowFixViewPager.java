package per.sue.gear3.widget.flow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ViewFlowFixViewPager extends ViewFlow {
	
	 private List<ViewGroup> parentViews = new ArrayList<>();

	public ViewFlowFixViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public ViewFlowFixViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	  public void addParentView(ViewGroup view) {
		  parentViews.add(view);
	    }

	    @Override
	    public boolean onInterceptTouchEvent(MotionEvent ev) {
	        if (parentViews != null)
	            switch (ev.getAction()) {
	                case MotionEvent.ACTION_DOWN:
						requestDisallowInterceptTouchEventForParent(true);
	                    break;
	                case MotionEvent.ACTION_UP:
	                    requestDisallowInterceptTouchEventForParent(false);
	                    break;
	                case MotionEvent.ACTION_CANCEL:
	                    requestDisallowInterceptTouchEventForParent(false);
	                    break;
	                case MotionEvent.ACTION_MOVE:
						requestDisallowInterceptTouchEventForParent(true);
	                    break;
	            }
	        return super.onInterceptTouchEvent(ev);
	    }

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		getParent().requestDisallowInterceptTouchEvent(true);//这句话的作用 告诉父view，我的单击事件我自行处理，不要阻碍我。
		return super.dispatchTouchEvent(ev);
	}

	private  void requestDisallowInterceptTouchEventForParent(boolean b){
		for(ViewGroup view : parentViews){
			view.requestDisallowInterceptTouchEvent(b);
		}
	}




}
