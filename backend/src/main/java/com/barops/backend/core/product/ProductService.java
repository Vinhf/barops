package com.barops.backend.core.product;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.barops.backend.core.auth.BranchAccessGuard;
import com.barops.backend.core.recipe.Recipe;
import com.barops.backend.core.recipe.RecipeRepository;
import com.barops.backend.core.tenant.Branch;
import com.barops.backend.core.tenant.BranchRepository;
import com.barops.backend.core.tenant.TenantContext;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    private final RecipeRepository recipeRepository;
    private final BranchAccessGuard branchAccessGuard;

    public ProductService(ProductRepository productRepository, BranchRepository branchRepository,
            RecipeRepository recipeRepository, BranchAccessGuard branchAccessGuard) {
        this.productRepository = productRepository;
        this.branchRepository = branchRepository;
        this.recipeRepository = recipeRepository;
        this.branchAccessGuard = branchAccessGuard;
    }

    public List<Product> findAll() {
        UUID branchId = TenantContext.getCurrentBranchId();
        if (branchId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Branch context is missing");
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

        return productRepository.findByBranch(branch);
    }

    public Product findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        branchAccessGuard.assertAccess(product.getBranch().getId());
        return product;
    }

    public Product create(ProductRequest request) {
        UUID branchId = TenantContext.getCurrentBranchId();
        if (branchId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Branch context is missing");
        }

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setBranch(branch);
        product.setRecipe(findRecipeForBranch(request.getRecipeId(), branch));

        return productRepository.save(product);
    }

    public Product update(UUID id, ProductRequest request) {
        Product existingProduct = findById(id);
        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());
        existingProduct.setRecipe(findRecipeForBranch(request.getRecipeId(), existingProduct.getBranch()));
        return productRepository.save(existingProduct);
    }

    public void delete(UUID id) {
        Product product = findById(id);
        productRepository.delete(product);
    }

    private Recipe findRecipeForBranch(UUID recipeId, Branch branch) {
        if (recipeId == null) {
            return null;
        }

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));

        if (!recipe.getBranch().getId().equals(branch.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Recipe does not belong to this branch");
        }

        return recipe;
    }
}