package jp.hotdrop.gmapp.fragment;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import jp.hotdrop.gmapp.activity.BaseActivity;
import jp.hotdrop.gmapp.di.FragmentComponent;
import jp.hotdrop.gmapp.di.FragmentModule;

public abstract class BaseFragment extends Fragment {

    protected static final int REQ_CODE_UPDATE = 1;
    protected static final int REQ_CODE_REGISTER = 2;

    protected static final String ARG_REFRESH_MODE = "refreshMode";

    protected static final int REFRESH_NONE = 0;
    protected static final int REFRESH_ONE = 1;
    protected static final int REFRESH_ALL = 2;

    private FragmentComponent fragmentComponent;

    @NonNull
    public FragmentComponent getComponent() {

        if(fragmentComponent != null) {
            return fragmentComponent;
        }

        Activity activity = getActivity();
        if(!(activity instanceof BaseActivity)) {
            throw new IllegalStateException("This fragment is not BaseActivity.");
        }

        fragmentComponent = ((BaseActivity) activity).getComponent().plus(new FragmentModule(this));
        return fragmentComponent;
    }
}
