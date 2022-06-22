// Generated code from Butter Knife. Do not modify!
package com.b4ebusinessdriver.Fragment;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.b4ebusinessdriver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class PastFragment_ViewBinding implements Unbinder {
  private PastFragment target;

  @UiThread
  public PastFragment_ViewBinding(PastFragment target, View source) {
    this.target = target;

    target.recyclerview = Utils.findRequiredViewAsType(source, R.id.recyclerview, "field 'recyclerview'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PastFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recyclerview = null;
  }
}
