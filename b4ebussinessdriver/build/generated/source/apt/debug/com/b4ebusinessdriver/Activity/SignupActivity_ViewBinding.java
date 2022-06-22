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

public class SignupActivity_ViewBinding implements Unbinder {
  private SignupActivity target;

  private View view2131362388;

  @UiThread
  public SignupActivity_ViewBinding(SignupActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SignupActivity_ViewBinding(final SignupActivity target, View source) {
    this.target = target;

    View view;
    target.name = Utils.findRequiredViewAsType(source, R.id.name, "field 'name'", EditText.class);
    target.nameError = Utils.findRequiredViewAsType(source, R.id.name_error, "field 'nameError'", TextView.class);
    target.email = Utils.findRequiredViewAsType(source, R.id.email, "field 'email'", EditText.class);
    target.emailError = Utils.findRequiredViewAsType(source, R.id.email_error, "field 'emailError'", TextView.class);
    target.mobile = Utils.findRequiredViewAsType(source, R.id.mobile, "field 'mobile'", EditText.class);
    target.mobileError = Utils.findRequiredViewAsType(source, R.id.mobile_error, "field 'mobileError'", TextView.class);
    target.password = Utils.findRequiredViewAsType(source, R.id.password, "field 'password'", EditText.class);
    target.passwordError = Utils.findRequiredViewAsType(source, R.id.password_error, "field 'passwordError'", TextView.class);
    target.bikenumber = Utils.findRequiredViewAsType(source, R.id.bikenumber, "field 'bikenumber'", TextView.class);
    target.bikenumberError = Utils.findRequiredViewAsType(source, R.id.bikenumber_error, "field 'bikenumberError'", TextView.class);
    view = Utils.findRequiredView(source, R.id.signup_btn, "field 'signupBtn' and method 'onViewClicked'");
    target.signupBtn = Utils.castView(view, R.id.signup_btn, "field 'signupBtn'", Button.class);
    view2131362388 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked();
      }
    });
    target.termsofuse = Utils.findRequiredViewAsType(source, R.id.termsofuse, "field 'termsofuse'", TextView.class);
    target.mobileExitsError = Utils.findRequiredViewAsType(source, R.id.mobile_exits_error, "field 'mobileExitsError'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SignupActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.name = null;
    target.nameError = null;
    target.email = null;
    target.emailError = null;
    target.mobile = null;
    target.mobileError = null;
    target.password = null;
    target.passwordError = null;
    target.bikenumber = null;
    target.bikenumberError = null;
    target.signupBtn = null;
    target.termsofuse = null;
    target.mobileExitsError = null;

    view2131362388.setOnClickListener(null);
    view2131362388 = null;
  }
}
