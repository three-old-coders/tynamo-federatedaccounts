package org.tynamo.security.federatedaccounts.services;

import org.apache.tapestry5.commons.Configuration;
import org.apache.tapestry5.commons.MappedConfiguration;
import org.apache.tapestry5.commons.services.Coercion;
import org.apache.tapestry5.commons.services.CoercionTuple;
import org.apache.tapestry5.commons.util.StringToEnumCoercion;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.services.LibraryMapping;
import org.tynamo.common.ModuleProperties;
import org.tynamo.security.federatedaccounts.FederatedAccountSymbols;
import org.tynamo.security.federatedaccounts.util.WindowMode;

import javax.swing.*;

public class FederatedAccountsModule {
	public static final String PATH_PREFIX = "federated";
	private static String version = ModuleProperties.getVersion(FederatedAccountsModule.class);

	public static void bind(ServiceBinder binder) {
		binder.bind(FederatedSignInComponentBlockSource.class, FederatedSignInComponentBlockSourceImpl.class);
		binder.bind(FederatedSignInOptions.class, FederatedSignInOptionsImpl.class);
	}

	public static void contributeFactoryDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(FederatedAccountSymbols.DEFAULT_RETURNPAGE, "");
		configuration.add(FederatedAccountSymbols.DEFAULT_REMEMBERME, "true");
		configuration.add(FederatedAccountSymbols.LOCALACCOUNT_REALMNAME, "");
	}

	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping(PATH_PREFIX, "org.tynamo.security.federatedaccounts"));
	}

//	public static void contributeClasspathAssetAliasManager(MappedConfiguration<String, String> configuration) {
//		configuration.add(PATH_PREFIX + "-" + version, "org/tynamo/security/federatedaccounts");
//	}

	public void contributeTypeCoercer(MappedConfiguration<CoercionTuple.Key, CoercionTuple> configuration)
	{
		final Coercion<String, WindowMode> coercion = new Coercion<String, WindowMode>()
		{
			public WindowMode coerce(String input)
			{
				return WindowMode.valueOf(input);
			}
		};

		final CoercionTuple<String, WindowMode> ct = CoercionTuple.create(String.class, WindowMode.class, coercion);
		configuration.add(ct.getKey(), ct);
	}
}
