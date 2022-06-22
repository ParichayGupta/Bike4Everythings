// Generated code from Butter Knife. Do not modify!
package com.b4ebusinessdriver.Activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.b4ebusinessdriver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ForgotPassActivity_ViewBinding implements Unbinder {
  private ForgotPassActivity target;

  private View view2131362344;

  @UiThread
  public ForgotPassActivity_ViewBinding(ForgotPassActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ForgotPassActivity_ViewBinding(final ForgotPassActivity target, View source) {
    this.target = target;

    View view;
    target.email = Utils.findRequiredViewAsType(source, R.id.email, "field 'email'", EditText.class);
    target.emailError = Utils.findRequiredViewAsType(source, R.id.email_error, "field 'emailError'", TextView.class);
    view = Utils.findRequiredView(source, R.id.resetpass_btn, "field 'resetpassBtn' and method 'onViewClicked'");
    target.resetpassBtn = Utils.castView(view, R.id.resetpass_btn, "field 'resetpassBtn'", Button.class);
    view2131362344 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    ForgotPassActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.email = null;
    target.emailError = null;
    target.resetpassBtn = null;

    view2131362344.setOnClickListener(null);
    view2131362344 = null;
  }
}
