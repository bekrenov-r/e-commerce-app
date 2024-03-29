package com.bekrenovr.ecommerce.catalog.filter;

import com.bekrenovr.ecommerce.catalog.dto.request.FilterOptions;
import com.bekrenovr.ecommerce.catalog.model.entity.Brand;
import com.bekrenovr.ecommerce.catalog.model.entity.Category;
import com.bekrenovr.ecommerce.catalog.model.entity.Item;
import com.bekrenovr.ecommerce.catalog.model.entity.Subcategory;
import com.bekrenovr.ecommerce.catalog.model.enums.Color;
import com.bekrenovr.ecommerce.catalog.model.enums.Gender;
import com.bekrenovr.ecommerce.catalog.model.enums.Material;
import com.bekrenovr.ecommerce.catalog.model.enums.Season;
import com.bekrenovr.ecommerce.catalog.repository.BrandRepository;
import com.bekrenovr.ecommerce.catalog.repository.CategoryRepository;
import com.bekrenovr.ecommerce.catalog.repository.ItemRepository;
import com.bekrenovr.ecommerce.catalog.repository.ItemSpecification;
import com.bekrenovr.ecommerce.catalog.util.RandomUtils;
import com.bekrenovr.ecommerce.catalog.util.dev.ItemGenerator;
import jakarta.transaction.Transactional;
import org.apache.commons.lang.math.DoubleRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class ItemSpecificationTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ItemGenerator itemGenerator;

    private static final DoubleRange PRICE_RANGE = new DoubleRange(10.0, 50.0);
    private static final List<String> SAMPLE_SIZES = Arrays.asList("XS", "S", "M", "L", "38", "39", "40");

    @BeforeEach
    void init(){
        Iterable<Item> testItems = itemGenerator.generateMultiple(50);
        itemRepository.saveAll(testItems);
    }

    @ParameterizedTest
    @EnumSource(Gender.class)
    public void testHasGender(Gender gender) {
        Specification<Item> spec = ItemSpecification.hasGender(gender);

        List<Item> items = itemRepository.findAll(spec);

        assertTrue(allItemsHaveGender(items, gender));
    }

    @Test
    public void testHasCategory() {
        Category category = RandomUtils.getRandomElement(categoryRepository.findAll());
        Specification<Item> spec = ItemSpecification.hasCategory(category);

        List<Item> items = itemRepository.findAll(spec);

        assertTrue(allItemsHaveCategory(items, category));
    }

    @Test
    public void testHasCategoryAndSubcategory() {
        Category category = getCategoryWithSubcategories();
        Subcategory subcategory = RandomUtils.getRandomElement(category.getSubcategories());
        Specification<Item> spec = ItemSpecification.hasCategoryAndSubcategory(category, subcategory);

        List<Item> items = itemRepository.findAll(spec);

        assertTrue(allItemsHaveCategoryAndSubcategory(items, category, subcategory));
    }

    @Test
    public void testHasPriceWithinRange() {
        Specification<Item> spec = ItemSpecification.hasPriceWithinRange(PRICE_RANGE);

        List<Item> items = itemRepository.findAll(spec);

        assertTrue(allItemsHavePriceWithinRange(items, PRICE_RANGE));
    }

    @Test
    @Transactional
    public void testHasSizeIn() {
        Specification<Item> spec = ItemSpecification.hasSizeIn(SAMPLE_SIZES);

        List<Item> items = itemRepository.findAll(spec);

        assertTrue(allItemsHaveSizeIn(items, SAMPLE_SIZES));
    }

    @Test
    public void testHasColorIn() {
        Collection<Color> colors = RandomUtils.getRandomSeries(Color.values(), 5);
        Specification<Item> spec = ItemSpecification.hasColorIn(colors);

        List<Item> items = itemRepository.findAll(spec);

        assertTrue(allItemsHaveColorIn(items, colors));
    }

    @Test
    @Transactional
    public void testHasBrandIn() {
        Collection<Brand> brands = RandomUtils.getRandomSeries(brandRepository.findAll(), 3);
        Specification<Item> spec = ItemSpecification.hasBrandIn(brands);

        List<Item> items = itemRepository.findAll(spec);

        assertTrue(allItemsHaveBrandIn(items, brands));
    }

    @Test
    public void testHasMaterialIn() {
        Collection<Material> materials = RandomUtils.getRandomSeries(Material.values(), 3);
        Specification<Item> spec = ItemSpecification.hasMaterialIn(materials);

        List<Item> items = itemRepository.findAll(spec);

        assertTrue(allItemsHaveMaterialIn(items, materials));
    }

    @Test
    public void testHasSeason() {
        Season season = RandomUtils.getRandomElement(Season.values());
        Specification<Item> spec = ItemSpecification.hasSeason(season);

        List<Item> items = itemRepository.findAll(spec);

        assertTrue(allItemsHaveSeason(items, season));
    }

    @Test
    public void testHasRatingGreaterThan() {
        Short rating = 4;
        Specification<Item> spec = ItemSpecification.hasRatingGreaterThan(rating);

        List<Item> items = itemRepository.findAll(spec);

        assertTrue(allItemsHaveRatingGreaterThan(items, rating));
    }

    @Test
    @Transactional
    public void testSpecificationComposition(){
        itemRepository.saveAll(itemGenerator.generateMultiple(100));
        var gender = RandomUtils.getRandomElement(Gender.values());
        var category = getCategoryWithSubcategories();
        var subcategory = RandomUtils.getRandomElement(category.getSubcategories());
        var sizeValues = RandomUtils.getRandomSeries(SAMPLE_SIZES, 4);
        var colors = RandomUtils.getRandomSeries(Color.values(), 3);
        var brands = RandomUtils.getRandomSeries(brandRepository.findAll(), 2);
        var materials = RandomUtils.getRandomSeries(Material.values(), 3);
        var season = RandomUtils.getRandomElement(Season.values());
        Short rating = 3;
        FilterOptions filterOptions = new FilterOptions(PRICE_RANGE, sizeValues, colors, brands, materials, season, rating);
        Specification<Item> compositeSpecification = Specification.allOf(
                ItemSpecification.hasGender(gender),
                ItemSpecification.hasCategoryAndSubcategory(category, subcategory),
                ItemSpecification.fromFilterOptions(filterOptions)
        );

        List<Item> items = itemRepository.findAll(compositeSpecification);

        assertAll(
                () -> allItemsHavePriceWithinRange(items, PRICE_RANGE),
                () -> allItemsHaveGender(items, gender),
                () -> allItemsHaveCategoryAndSubcategory(items, category, subcategory),
                () -> allItemsHaveSizeIn(items, sizeValues),
                () -> allItemsHaveColorIn(items, colors),
                () -> allItemsHaveBrandIn(items, brands),
                () -> allItemsHaveMaterialIn(items, materials),
                () -> allItemsHaveSeason(items, season),
                () -> allItemsHaveRatingGreaterThan(items, rating)
        );
    }

    private boolean allItemsHaveGender(List<Item> items, Gender gender){
        return items.stream()
                .allMatch(i -> i.getGender().equals(gender));
    }

    private boolean allItemsHaveCategory(List<Item> items, Category category){
        return items.stream()
                .allMatch(i -> i.getCategory().equals(category));
    }

    private boolean allItemsHaveCategoryAndSubcategory(List<Item> items, Category category, Subcategory subcategory){
        return items.stream()
                .allMatch(i -> i.getCategory().equals(category) && i.getSubcategory().equals(subcategory));
    }

    private boolean allItemsHavePriceWithinRange(List<Item> items, DoubleRange priceRange){
        return items.stream()
                .allMatch(i -> priceRange.containsDouble(i.getPriceAfterDiscount()));
    }

    private boolean allItemsHaveSizeIn(List<Item> items, Collection<String> sizeValues){
        Predicate<Item> hasSize = item ->
                item.getUniqueItems()
                        .stream()
                        .anyMatch(ui -> sizeValues.contains(ui.getSize()));
        return items.stream().allMatch(hasSize);
    }

    private boolean allItemsHaveColorIn(List<Item> items, Collection<Color> colors){
        return items.stream().allMatch(i -> colors.contains(i.getColor()));
    }

    private boolean allItemsHaveBrandIn(List<Item> items, Collection<Brand> brands){
        return items.stream().allMatch(i -> brands.contains(i.getBrand()));
    }

    private boolean allItemsHaveMaterialIn(List<Item> items, Collection<Material> materials){
        return items.stream().allMatch(i -> materials.contains(i.getMaterial()));
    }

    private boolean allItemsHaveSeason(List<Item> items, Season season){
        return items.stream().allMatch(i -> i.getSeason().equals(season));
    }

    private boolean allItemsHaveRatingGreaterThan(List<Item> items, Short rating){
        return items.stream().allMatch(i -> i.getRating() > rating);
    }

    private Category getCategoryWithSubcategories(){
        return categoryRepository.findAll().stream()
                .filter(c -> !c.getSubcategories().isEmpty())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("At least one subcategory is required"));
    }
}

