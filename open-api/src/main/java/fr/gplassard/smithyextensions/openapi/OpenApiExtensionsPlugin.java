package fr.gplassard.smithyextensions.openapi;

import fr.gplassard.smithyextensions.openapi.mappers.OpenApiTypeMapper;
import software.amazon.smithy.jsonschema.JsonSchemaMapper;
import software.amazon.smithy.openapi.fromsmithy.Smithy2OpenApiExtension;

import java.util.List;

public class OpenApiExtensionsPlugin implements Smithy2OpenApiExtension {
    @Override
    public List<JsonSchemaMapper> getJsonSchemaMappers() {
        return List.of(
                new OpenApiTypeMapper()
        );
    }
}
