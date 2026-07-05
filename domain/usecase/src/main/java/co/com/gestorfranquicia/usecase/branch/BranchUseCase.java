package co.com.gestorfranquicia.usecase.branch;

import co.com.gestorfranquicia.model.branch.Branch;
import co.com.gestorfranquicia.model.branch.gateways.BranchRepository;
import co.com.gestorfranquicia.model.enums.TechnicalMessage;
import co.com.gestorfranquicia.model.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchUseCase {

    private final BranchRepository branchRepository;

    public Mono<Branch> create(String name, Long franchiseId) {
        return branchRepository.validateForCreation(name, franchiseId)
                .flatMap(check -> switch (check) {
                    case PARENT_NOT_FOUND -> Mono.<Branch>error(new BusinessException(TechnicalMessage.FRANCHISE_NOT_FOUND));
                    case ALREADY_EXISTS -> Mono.<Branch>error(new BusinessException(TechnicalMessage.BRANCH_ALREADY_EXISTS));
                    case ALLOWED -> branchRepository.save(Branch.create(name, franchiseId));
                });
    }

    public Mono<Void> updateName(Long branchId, String newName) {
        return branchRepository.validateForRename(newName, branchId)
                .flatMap(check -> switch (check) {
                    case NOT_FOUND -> Mono.<Void>error(new BusinessException(TechnicalMessage.BRANCH_NOT_FOUND));
                    case ALREADY_EXISTS -> Mono.<Void>error(new BusinessException(TechnicalMessage.BRANCH_ALREADY_EXISTS));
                    case ALLOWED -> Mono.fromCallable(() -> Branch.builder().id(branchId).build().changeName(newName))
                            .flatMap(renamed -> branchRepository.updateName(renamed.getId(), renamed.getName()));
                });
    }
}
