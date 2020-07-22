package by.aermakova.prettysip.ui.activity.linphone;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import io.reactivex.disposables.CompositeDisposable;
import by.aermakova.prettysip.logic.implementation.ICallLogic;

/**
 * logic implementation of {@link IncomingCallFragment} via {@link ICallLogic}
 */
public class IncomingCallPresenter implements ICallLogic.Presenter, LifecycleObserver {

    private CompositeDisposable mDisposable = new CompositeDisposable();

    IncomingCallPresenter(ICallLogic.View view, Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroyFragment() {
        mDisposable.dispose();
    }
}