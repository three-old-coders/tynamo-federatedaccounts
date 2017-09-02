package org.tynamo.security.federatedaccounts.testapp.services;

import java.sql.SQLException;

import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.federatedaccounts.FederatedAccount.FederatedAccountType;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.facebook.services.FacebookFederatedAccountsModule;
import org.tynamo.security.federatedaccounts.pac4j.services.Pac4jOauthClientLocator.SupportedClient;
import org.tynamo.security.federatedaccounts.services.DefaultJpaFederatedAccountServiceImpl;
import org.tynamo.security.federatedaccounts.services.FederatedAccountService;
import org.tynamo.security.federatedaccounts.services.FederatedAccountsModule;
import org.tynamo.security.federatedaccounts.services.FederatedSignInOptions;
import org.tynamo.security.federatedaccounts.services.FederatedSignInOptions.OptionType;
import org.tynamo.security.federatedaccounts.testapp.entities.User;
import org.tynamo.security.rollingtokens.services.RollingTokenRealm;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.SecurityModule;
import org.tynamo.security.services.impl.SecurityFilterChain;
import org.tynamo.seedentity.jpa.services.SeedEntityModule;

// excplicitly claiming these as submodules just for development and testing
@SubModule(value = { SecurityModule.class, SeedEntityModule.class, FederatedAccountsModule.class, FacebookFederatedAccountsModule.class })
public class AppModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(FederatedAccountService.class, DefaultJpaFederatedAccountServiceImpl.class);
		binder.bind(AuthorizingRealm.class, UserRealm.class).withId(UserRealm.class.getSimpleName());
	}

	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		// Contributions to ApplicationDefaults will override any contributions to
		// FactoryDefaults (with the same key). Here we're restricting the supported
		// locales to just "en" (English). As you add localised message catalogs and other assets,
		// you can extend this list of locales (it's a comma separated series of locale names;
		// the first locale name is the default when there's no reasonable match).

		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");

		// The factory default is true but during the early stages of an application
		// overriding to false is a good idea. In addition, this is often overridden
		// on the command line as -Dtapestry.production-mode=false
		configuration.add(SymbolConstants.PRODUCTION_MODE, "false");

		// The application version number is incorprated into URLs for some
		// assets. Web browsers will cache assets because of the far future expires
		// header. If existing assets are changed, the version number should also
		// change, to force the browser to download new versions.
		configuration.add(SymbolConstants.APPLICATION_VERSION, "0.0.1-SNAPSHOT");

		configuration.add(FederatedAccountSymbols.LOCALACCOUNT_REALMNAME, "localaccounts");

		configuration.add(SecuritySymbols.LOGIN_URL, "/login");
		configuration.add(SecuritySymbols.SUCCESS_URL, "/index");

//		configuration.add(Pac4jFederatedRealm.YAHOO_CLIENTID, "somekey");
//		configuration.add(Pac4jFederatedRealm.YAHOO_CLIENTSECRET, "just to test yahoo login icon");
	}

	public static void contributeSecurityConfiguration(OrderedConfiguration<SecurityFilterChain> configuration,
			SecurityFilterChainFactory factory) {
		configuration.add("assets", factory.createChain("/assets/**").add(factory.anon()).build());
		configuration.add("loginform-anon",
			factory.createChain("/login.loginform.tynamologinform").add(factory.anon()).build());
		configuration.add("federated-anon", factory.createChain("/federated/**").add(factory.anon()).build());
		configuration.add("modules-anon", factory.createChain("/modules/**").add(factory.anon()).build());
		configuration.add("modules.gz-anon", factory.createChain("/modules.gz/**").add(factory.anon()).build());
		configuration.add("authc", factory.createChain("/**").add(factory.authc()).build());
	}

	public static void contributeWebSecurityManager(Configuration<Realm> configuration, @InjectService("UserRealm") AuthorizingRealm userRealm) {
		// FacebookRealm is automatically contributed as long as federatedsecurity is on the classpath
		configuration.add(userRealm);
	}

	public static void contributeSeedEntity(OrderedConfiguration<Object> configuration) {
		User localUser = new User();
		localUser.setUsername("user");
		localUser.setFirstName("Local");
		localUser.setLastName("User");
		localUser.setPassword("user");
		configuration.add("localuser", localUser);
		User fakeFederatedUser = new User();
		fakeFederatedUser.setUsername("fbuser");
		fakeFederatedUser.setFirstName("Facebook");
		fakeFederatedUser.setLastName("User");
		fakeFederatedUser.setFacebookUserId(0L);
		configuration.add("fakeuser", fakeFederatedUser);
	}

	@Contribute(FederatedSignInOptions.class)
	public static void provideDefaultSignInBlocks(MappedConfiguration<String,OptionType> configuration) {
		 configuration.add(FederatedAccountType.facebook.name(), OptionType.primary);
		 configuration.add(FederatedAccountType.twitter.name(), OptionType.primary);
		 configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.facebook.name(), OptionType.secondary);
		 configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.dropbox.name(), OptionType.secondary);
		 configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.github.name(), OptionType.secondary);
		 configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.google2.name(), OptionType.secondary);
		 configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.linkedin2.name(), OptionType.secondary);
		 configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.twitter.name(), OptionType.secondary);
		 configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.windowslive.name(), OptionType.secondary);
		 configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.wordpress.name(), OptionType.secondary);
		 configuration.add(FederatedAccountType.pac4j_.name() + SupportedClient.yahoo.name(), OptionType.secondary);
	}

	public static void contributeFederatedAccountService(MappedConfiguration<String, Object> configuration) {
		configuration.add("*", User.class);
		configuration.add("facebook.id", "facebookId");
		configuration.add("twitter.id", "twitterId");
		configuration.add("pac4j_dropbox.id", "dropboxId");
		configuration.add("pac4j_yahoo.id", "yahooId");
		configuration.add(RollingTokenRealm.NAME + FederatedAccountService.IDPROPERTY, "id");
	}

	@Startup
	public static void startH2WebServer()
		throws SQLException {
			org.h2.tools.Server.createWebServer(new String[] { "-web", "-webAllowOthers", "-webPort", "8082" }).start();
	}


}
