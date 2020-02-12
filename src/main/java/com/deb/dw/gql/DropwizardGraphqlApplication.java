package com.deb.dw.gql;

import java.util.EnumSet;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import com.deb.dw.gql.api.SayingDataFetcher;
import com.deb.dw.gql.health.TemplateHealthCheck;
import com.smoketurner.dropwizard.graphql.GraphQLBundle;
import com.smoketurner.dropwizard.graphql.GraphQLFactory;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import graphql.schema.idl.RuntimeWiring;

public class DropwizardGraphqlApplication extends Application<DropwizardGraphqlConfiguration> {

    public static void main(final String[] args) throws Exception {
        new DropwizardGraphqlApplication().run(args);
    }

    @Override
    public String getName() {
        return "DropwizardGraphql";
    }

    @Override
    public void initialize(final Bootstrap<DropwizardGraphqlConfiguration> bootstrap) {
        final GraphQLBundle<DropwizardGraphqlConfiguration> bundle =
                new GraphQLBundle<DropwizardGraphqlConfiguration>() {
                    @Override
                    public GraphQLFactory getGraphQLFactory(
                            DropwizardGraphqlConfiguration configuration) {

                        final GraphQLFactory factory = configuration.getGraphQLFactory();
                        // the RuntimeWiring must be configured prior to the run()
                        // methods being called so the schema is connected properly.
                        factory.setRuntimeWiring(buildWiring(configuration));
                        return factory;
                    }
                };
        bootstrap.addBundle(bundle);
    }

    @Override
    public void run(final DropwizardGraphqlConfiguration configuration,
            final Environment environment) {

        // Enable CORS to allow GraphiQL on a separate port to reach the API
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("cors", CrossOriginFilter.class);
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
    }

    private static RuntimeWiring buildWiring(DropwizardGraphqlConfiguration configuration) {

        final SayingDataFetcher fetcher =
                new SayingDataFetcher(configuration.getTemplate(), configuration.getDefaultName());

        final RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring.dataFetcher("saying", fetcher)).build();

        return wiring;
    }

}
