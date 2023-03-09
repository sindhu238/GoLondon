package com.srisindhusaride.golondon.DI.Module;

import android.app.Application;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvidesApplicationFactory implements Factory<Application> {
  private final AppModule module;

  public AppModule_ProvidesApplicationFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public Application get() {
    return providesApplication(module);
  }

  public static AppModule_ProvidesApplicationFactory create(AppModule module) {
    return new AppModule_ProvidesApplicationFactory(module);
  }

  public static Application providesApplication(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.providesApplication());
  }
}
