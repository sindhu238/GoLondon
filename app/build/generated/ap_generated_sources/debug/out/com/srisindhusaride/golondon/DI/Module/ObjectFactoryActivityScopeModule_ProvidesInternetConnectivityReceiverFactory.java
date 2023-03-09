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
public final class ObjectFactoryActivityScopeModule_ProvidesInternetConnectivityReceiverFactory implements Factory<InternetConnectivityReceiver> {
  private final ObjectFactoryActivityScopeModule module;

  public ObjectFactoryActivityScopeModule_ProvidesInternetConnectivityReceiverFactory(
      ObjectFactoryActivityScopeModule module) {
    this.module = module;
  }

  @Override
  public InternetConnectivityReceiver get() {
    return providesInternetConnectivityReceiver(module);
  }

  public static ObjectFactoryActivityScopeModule_ProvidesInternetConnectivityReceiverFactory create(
      ObjectFactoryActivityScopeModule module) {
    return new ObjectFactoryActivityScopeModule_ProvidesInternetConnectivityReceiverFactory(module);
  }

  public static InternetConnectivityReceiver providesInternetConnectivityReceiver(
      ObjectFactoryActivityScopeModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.providesInternetConnectivityReceiver());
  }
}
