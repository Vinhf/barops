CREATE TABLE IF NOT EXISTS businesses (
    id UUID PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS branches (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    business_id UUID NOT NULL,
    CONSTRAINT fk_branches_business FOREIGN KEY (business_id) REFERENCES businesses(id)
);

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    business_id UUID,
    branch_id UUID,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price NUMERIC(19, 6),
    stock INTEGER,
    branch_id UUID NOT NULL,
    recipe_id UUID,
    CONSTRAINT fk_products_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
);

CREATE TABLE IF NOT EXISTS ingredients (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    unit VARCHAR(255) NOT NULL,
    cost_price NUMERIC(19, 6),
    branch_id UUID NOT NULL,
    CONSTRAINT fk_ingredients_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
);

CREATE TABLE IF NOT EXISTS recipes (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    branch_id UUID NOT NULL,
    CONSTRAINT fk_recipes_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
);

CREATE TABLE IF NOT EXISTS recipe_ingredients (
    id UUID PRIMARY KEY,
    recipe_id UUID NOT NULL,
    ingredient_id UUID NOT NULL,
    quantity NUMERIC(19, 6),
    unit VARCHAR(255),
    CONSTRAINT fk_recipe_ingredients_recipe FOREIGN KEY (recipe_id) REFERENCES recipes(id),
    CONSTRAINT fk_recipe_ingredients_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

CREATE TABLE IF NOT EXISTS branch_ingredient_stock (
    id UUID PRIMARY KEY,
    branch_id UUID NOT NULL,
    ingredient_id UUID NOT NULL,
    quantity NUMERIC(19, 6),
    minimum_quantity NUMERIC(19, 6),
    CONSTRAINT uq_branch_ingredient_stock UNIQUE (branch_id, ingredient_id),
    CONSTRAINT fk_stock_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_stock_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

CREATE TABLE IF NOT EXISTS branch_menu_items (
    id UUID PRIMARY KEY,
    branch_id UUID NOT NULL,
    product_id UUID NOT NULL,
    selling_price NUMERIC(19, 6),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_branch_menu_product UNIQUE (branch_id, product_id),
    CONSTRAINT fk_menu_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_menu_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS inventory_transactions (
    id UUID PRIMARY KEY,
    branch_id UUID NOT NULL,
    ingredient_id UUID NOT NULL,
    operation VARCHAR(20) NOT NULL,
    quantity NUMERIC(19, 6) NOT NULL,
    before_quantity NUMERIC(19, 6) NOT NULL,
    after_quantity NUMERIC(19, 6) NOT NULL,
    reason VARCHAR(255),
    created_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_transactions_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT fk_transactions_ingredient FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_products_recipe'
    ) THEN
        ALTER TABLE products
            ADD CONSTRAINT fk_products_recipe
            FOREIGN KEY (recipe_id) REFERENCES recipes(id);
    END IF;
END $$;