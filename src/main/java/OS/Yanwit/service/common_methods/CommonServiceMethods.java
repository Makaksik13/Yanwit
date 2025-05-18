package OS.Yanwit.service.common_methods;

import OS.Yanwit.exception.NotFoundException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

@Component
public class CommonServiceMethods {
    public <T> T findEntityById(CrudRepository<T, Long> repository, long id, String entityName) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("%s with id %d not found", entityName, id)));
    }
}
