package per.sue.gear3.ui;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import per.sue.gear3.utils.GearLog;


/**
 * Created by SUE on 2016/7/7 0007.
 */
public abstract class GearFragment extends Fragment implements GearView {
    protected   final String TAG = this.getClass().getSimpleName();
    protected   GearViewHelper gearViewHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View view = inflater.inflate(getLayoutResId(), null);
         ButterKnife.bind(this, view);
        gearViewHelper = new GearViewHelper(getActivity(), getChildFragmentManager());
        GearLog.e(TAG, " onCreateView();");
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GearLog.e(TAG, " onDestroyView();");
        ButterKnife.unbind(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        GearLog.e(TAG, " isVisibleToUser();" + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onInitialize(savedInstanceState);
    }


}
