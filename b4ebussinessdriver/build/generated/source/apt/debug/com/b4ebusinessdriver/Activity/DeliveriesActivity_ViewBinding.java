// Generated code from Butter Knife. Do not modify!
package com.b4ebusinessdriver.Activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.b4ebusinessdriver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class DeliveriesActivity_ViewBinding implements Unbinder {
  private DeliveriesActivity target;

  @UiThread
  public DeliveriesActivity_ViewBinding(DeliveriesActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public DeliveriesActivity_ViewBinding(DeliveriesActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.recyclerview = Utils.findRequiredViewAsType(source, R.id.recyclerview, "field 'recyclerview'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    DeliveriesActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.recyclerview = null;
  }
}
