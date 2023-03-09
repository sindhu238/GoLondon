package com.srisindhusaride.golondon.DI.Module;

import com.srisindhusaride.golondon.Utils.InternetConnectivityReceiver;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.Generated;

@ScopeMetadata("com.srisindhusaride.golondon.DI.Scope.ActivityScope")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class CheckInternetModule_ProvidesInternetConnectivityReceiverFactory implements Factory<InternetConnectivityReceiver> {
  private final CheckInternetModule module;

  public CheckInternetModule_ProvidesInternetConnectivityReceiverFactory(
      CheckInternetModule module) {
    this.module = module;
  }

  @Override
  public InternetConnectivityReceiver get() {
    return providesInternetConnectivityReceiver(module);
  }

  public static CheckInternetModule_ProvidesInternetConnectivityReceiverFactory create(
      CheckInternetModule module) {
    return new CheckInternetModule_ProvidesInternetConnectivityReceiverFactory(module);
  }

  public static InternetConnectivityReceiver providesInternetConnectivityReceiver(
      CheckInternetModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.providesInternetConnectivityReceiver());
  }
}
