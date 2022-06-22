// Generated code from Butter Knife. Do not modify!
package com.b4ebusinessdriver.Activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.b4ebusinessdriver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class NewOrderActivity_ViewBinding implements Unbinder {
  private NewOrderActivity target;

  private View view2131362341;

  private View view2131361801;

  @UiThread
  public NewOrderActivity_ViewBinding(NewOrderActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public NewOrderActivity_ViewBinding(final NewOrderActivity target, View source) {
    this.target = target;

    View view;
    target.txtName = Utils.findRequiredViewAsType(source, R.id.txtName, "field 'txtName'", TextView.class);
    target.pickupMobile = Utils.findRequiredViewAsType(source, R.id.pickupMobile, "field 'pickupMobile'", TextView.class);
    target.pickupAddress = Utils.findRequiredViewAsType(source, R.id.pickupAddress, "field 'pickupAddress'", TextView.class);
    view = Utils.findRequiredView(source, R.id.reject, "field 'reject' and method 'onRejectClicked'");
    target.reject = Utils.castView(view, R.id.reject, "field 'reject'", Button.class);
    view2131362341 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onRejectClicked();
      }
    });
    view = Utils.findRequiredView(source, R.id.accept, "field 'accept' and method 'onAcceptClicked'");
    target.accept = Utils.castView(view, R.id.accept, "field 'accept'", Button.class);
    view2131361801 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onAcceptClicked();
      }
    });
    target.businessname = Utils.findRequiredViewAsType(source, R.id.businessname, "field 'businessname'", TextView.class);
    target.amount = Utils.findRequiredViewAsType(source, R.id.amount, "field 'amount'", TextView.class);
    target.battery = Utils.findRequiredViewAsType(source, R.id.battery, "field 'battery'", TextView.class);
    target.network = Utils.findRequiredViewAsType(source, R.id.network, "field 'network'", TextView.class);
    target.gpssignal = Utils.findRequiredViewAsType(source, R.id.gpssignal, "field 'gpssignal'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    NewOrderActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.txtName = null;
    target.pickupMobile = null;
    target.pickupAddress = null;
    target.reject = null;
    target.accept = null;
    target.businessname = null;
    target.amount = null;
    target.battery = null;
    target.network = null;
    target.gpssignal = null;

    view2131362341.setOnClickListener(null);
    view2131362341 = null;
    view2131361801.setOnClickListener(null);
    view2131361801 = null;
  }
}
