package com.barops.backend.core.inventory;

import java.math.BigDecimal;
import java.util.UUID;

import com.barops.backend.core.ingredient.Ingredient;
import com.barops.backend.core.tenant.Branch;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "branch_ingredient_stock", uniqueConstraints = @UniqueConstraint(columnNames = { "branch_id", "ingredient_id" }))
public class BranchIngredientStock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    private BigDecimal quantity = BigDecimal.ZERO;
    private BigDecimal minimumQuantity = BigDecimal.ZERO;

    public UUID getId() {
        return id;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(BigDecimal minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }
}
