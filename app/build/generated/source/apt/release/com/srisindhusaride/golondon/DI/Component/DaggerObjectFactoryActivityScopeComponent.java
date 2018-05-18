package com.srisindhusaride.golondon.DI.Component;

import com.srisindhusaride.golondon.DI.Module.ObjectFactoryActivityScopeModule;
import com.srisindhusaride.golondon.DI.Module.ObjectFactoryActivityScopeModule_ProvidesNearByFragmentFactory;
import com.srisindhusaride.golondon.MainActivity;
import com.srisindhusaride.golondon.MainActivity_MembersInjector;
import com.srisindhusaride.golondon.Nearby_Fragment;
import dagger.MembersInjector;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerObjectFactoryActivityScopeComponent
    implements ObjectFactoryActivityScopeComponent {
  private Provider<Nearby_Fragment> providesNearByFragmentProvider;

  private MembersInjector<MainActivity> mainActivityMembersInjector;

  private DaggerObjectFactoryActivityScopeComponent(Builder builder) {
    assert builder != null;
    initialize(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static ObjectFactoryActivityScopeComponent create() {
    return builder().build();
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {

    this.providesNearByFragmentProvider =
        DoubleCheck.provider(
            ObjectFactoryActivityScopeModule_ProvidesNearByFragmentFactory.create(
                builder.objectFactoryActivityScopeModule));

    this.mainActivityMembersInjector =
        MainActivity_MembersInjector.create(providesNearByFragmentProvider);
  }

  @Override
  public void inject(MainActivity mainActivity) {
    mainActivityMembersInjector.injectMembers(mainActivity);
  }

  public static final class Builder {
    private ObjectFactoryActivityScopeModule objectFactoryActivityScopeModule;

    private Builder() {}

    public ObjectFactoryActivityScopeComponent build() {
      if (objectFactoryActivityScopeModule == null) {
        this.objectFactoryActivityScopeModule = new ObjectFactoryActivityScopeModule();
      }
      return new DaggerObjectFactoryActivityScopeComponent(this);
    }

    public Builder objectFactoryActivityScopeModule(
        ObjectFactoryActivityScopeModule objectFactoryActivityScopeModule) {
      this.objectFactoryActivityScopeModule =
          Preconditions.checkNotNull(objectFactoryActivityScopeModule);
      return this;
    }
  }
}
