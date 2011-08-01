package org.tynamo.security.federatedaccounts.twitter.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.tynamo.security.federatedaccounts.twitter.base.TwitterOauthComponentBase;
import org.tynamo.security.federatedaccounts.util.WindowMode;

@Import(library = "TwitterSignIn.js", stylesheet = "fb-button.css")
public class TwitterSignIn extends TwitterOauthComponentBase {

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "800")
	@Property
	private Integer width;

	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "400")
	@Property
	private Integer height;

	@Parameter(value = "blank", required = false, defaultPrefix = "literal")
	private WindowMode windowMode;

	public boolean isWindowMode(String mode) {
		if (mode == null) throw new IllegalArgumentException("Window mode argument cannot be null");
		return windowMode.equals(WindowMode.valueOf(mode));
	}

	public String getOauthAuthorizationLink() {
		StringBuilder sb = new StringBuilder();
		sb.append("https://graph.facebook.com/oauth/authorize?client_id=");
		sb.append(getOauthClientId());
		sb.append("&redirect_uri=");
		sb.append(getOauthRedirectLink(windowMode));
		sb.append("&display=popup");
		sb.append("&scope=");
//		sb.append(facebookPermissions);

		return sb.toString();
	}
}