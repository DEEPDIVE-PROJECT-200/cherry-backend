package ok.cherry.product.application;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ok.cherry.product.application.dto.request.ProductCreateRequest;
import ok.cherry.product.domain.type.Brand;
import ok.cherry.product.domain.type.Color;
import ok.cherry.product.infrastructure.ProductRepository;

@Component
@RequiredArgsConstructor
@Transactional
@Profile("dev")
public class ProductDataInitializer implements CommandLineRunner {

	private final ProductRepository productRepository;
	private final ProductCreateService productCreateService;

	@Override
	public void run(String... args) {
		saveProduct(
			"WH-1000XM6",
			Brand.SONY,
			List.of(Color.PLATINUM_SILVER, Color.BLACK, Color.MIDNIGHT_BLUE),
			500L,
			"2025-06-18",
			List.of(
				"8d1146b1-9285-4d02-8712-3b3595273baa_01.jpg",
				"d6246eb7-66d8-4b07-bb46-c92e986b4ad4_02.jpg",
				"add4d45a-5ead-4f03-8ff2-5b610641ce85_03.jpg",
				"01065b10-5cb9-4204-b537-cb9605a1b1b0_04.jpg",
				"438bedaa-cf49-4cfe-ad96-a726000117fb_05.jpg",
				"8146da73-7fe1-427f-9c00-befd65457475_06.jpg",
				"75c3fdde-23c7-4f6e-a89b-a3f7a552cf8e_07.jpg",
				"448e5412-87af-43ad-a37d-db9f13cd866e_08.jpg"
			),
			List.of(
				"b0dfab25-3c05-4d28-8456-cb12b2835c2e_01.jpg",
				"f5521f87-a1b8-4528-9317-1bd25e6fdf45_02.jpg",
				"094f344c-5a55-43df-83f5-eb4902874c57_03.jpg",
				"3bd3623d-d6e0-476d-ae92-72372a2ba398_04.jpg",
				"8e9714a5-f373-46ea-876a-e11b48907dd6_05.jpg",
				"8845abcb-57c2-49f4-88ea-d5951e5d6c18_06.jpg",
				"51ebce63-b607-4402-ab77-980be5081d04_07.jpg",
				"f5cb0c62-5205-4e0d-9e56-28fa62486264_08.jpg",
				"f79727ae-1b6b-48c1-87c0-5fda4365ec13_09.jpg",
				"647db2da-3e50-45e5-bb3b-8b16680f4c33_10.jpg",
				"5e581630-2238-4f21-a994-b3159412f020_11.jpg",
				"50ae6abc-88a6-4c91-89f3-d8ba8b3ce7ec_12.jpg"
			)
		);

		saveProduct(
			"AirPods Max",
			Brand.APPLE,
			List.of(Color.MIDNIGHT, Color.STARLIGHT, Color.BLUE, Color.PURPLE, Color.ORANGE),
			500L,
			"2020-12-15",
			List.of(
				"2c3da6f1-4acf-46c2-a07c-5c2b7ccb179e_01.jpg",
				"fedc63ea-bbcf-4a98-a592-f95d8ec9b191_02.jpg",
				"9cb1b92d-9cf8-4339-bf1f-daba6941521c_03.jpg",
				"4a09c40a-1903-45be-b2d2-3a66ef729441_04.jpg",
				"018b6a08-0446-49af-a079-2a5eadaf5f33_05.jpg",
				"c5834c8a-74a6-4ff9-a5a7-f25fb2329462_06.jpg",
				"e511aa87-9fa0-48ac-aae8-3f7098c57511_07.jpg",
				"a1084c75-eef4-4708-8a6d-12073a41d159_08.jpg",
				"2be11e57-67dc-48c8-8d7c-d739db9c4838_09.jpg",
				"836cbe49-82ff-41ee-9a5e-2117f5b7ae1d_10.jpg"
			),
			List.of(
				"9bd324e6-e8a9-4f3e-a1f4-d53cfabf96b3_Image.jpg"
			)
		);

		saveProduct(
			"QuietComfort Ultra Headphones",
			Brand.BOSE,
			List.of(Color.BLACK, Color.WHITE_SMOKE, Color.LUNA_BLUE, Color.DEEP_PLUM, Color.SANDSTONE),
			500L,
			"2023-10-16",
			(List.of(
				"c318e0ea-acc5-4c7f-9f05-736a2cf61408_01.jpg",
				"b87bb35f-2137-49e6-b11d-f133476ffa92_02.jpg",
				"53a0ab75-e7e4-4f58-8537-96c1b2e37701_03.jpg",
				"88a9af42-a660-43a5-aebd-247d22402a76_04.jpg",
				"a5aa6778-09d8-4c46-9ce7-f13cb737f1e9_05.jpg",
				"e721216b-10b9-45b4-981a-76c9e01523cf_06.jpg",
				"df0fe5cd-703c-4b56-b515-35ea1ab05ec3_07.jpg"
			)),
			List.of(
				"c46abc6f-cd96-4113-9be6-df268a5b2a0d_01.jpg",
				"f0ea7a40-3774-4375-92b0-12102e4a9962_02.jpg",
				"a3116fc6-349e-45d4-9004-bd8c5f050349_03.jpg",
				"802f3ed1-60a1-4db6-8b8c-745f086b77fb_04.jpg",
				"60598c08-61d3-4d9d-85b5-3f3080e3d58c_05.jpg"
			)
		);

		saveProduct(
			"MOMENTUM 4 Wireless",
			Brand.SENNHEISER,
			List.of(Color.BLACK, Color.WHITE, Color.GRAPHITE),
			500L,
			"2022-08-23",
			List.of(
				"3fd393c8-59a8-4e63-83bb-aa8d3525376b_01.jpg",
				"efcf6fa1-d9e5-41c6-812d-44fadac34e15_02.jpg",
				"7c5db168-5c7b-4263-86d7-3863c8112333_03.jpg",
				"999809e9-4213-4472-bc67-cd49ece5c2b5_04.jpg",
				"6ff81d77-35c8-4346-93f3-70266299fe2f_05.jpg",
				"cde6493f-c551-4da1-9246-0b2b21e8669b_06.jpg"
			),
			List.of(
				"7f5a83e6-2e1b-4b70-875b-cfb919df4f59_01.jpg",
				"2f7ba66a-9104-45e4-a4a3-5974dcf6f311_02.jpg",
				"f08fcf87-4e44-44ed-921d-eafbeb0a2f04_03.jpg",
				"76f795cc-61d5-46c1-99ba-1e3708fc0e25_04.jpg",
				"912c2918-5ad5-40da-ac79-7a5396f19904_05.jpg"
			)
		);

		saveProduct(
			"Beoplay HX",
			Brand.BANG_OLUFSEN,
			List.of(Color.BLACK, Color.GOLD, Color.TIMBER),
			500L,
			"2021-04-06",
			List.of(
				"a6398f37-bd66-4685-b554-d7c2d2e36e62_01.jpg",
				"c3b9852a-b5cc-4214-9e2b-1ea49529f95f_02.jpg",
				"27e74464-175d-4f5f-8032-fb5b8da5bbc1_03.jpg",
				"b99b013f-32c3-4ae0-8f41-3cb7c23889e8_04.jpg",
				"9a1c61a6-0353-4fe8-b3f4-c9f801b98bf0_05.jpg",
				"f0f2e471-1c97-4487-9c14-0dc259215b57_06.jpg",
				"6f6b9c25-c777-4674-8faf-f0b8d157d728_07.jpg"
			),
			List.of(
				"b5fbd3fb-70d3-451d-b13c-159ffe15c7a4_01.jpg",
				"7662bbcf-7c90-47cd-9ad9-c8267ba51cc6_02.jpg",
				"f8779fc0-e670-413a-8dc1-37b84a5ae7db_03.jpg",
				"576276b5-ca60-4e48-8af3-9fbb4b00f142_04.jpg",
				"d1400281-9e83-4e4c-9f47-69ee5f5cd88e_05.jpg",
				"8a91fe02-19ae-4fce-9e7c-55a363fea044_06.jpg",
				"9a771663-1ece-424c-9be6-34a41755fa5d_07.jpg",
				"b10e63ee-d7b9-4878-82ed-fc76afe69574_08.jpg",
				"4cd5ac6c-974a-4cc1-bd1f-7d62afec6431_09.jpg",
				"52bbe82f-f0c6-4362-a882-81713544ccbf_10.jpg"
			)
		);

		saveProduct(
			"Px8",
			Brand.BOWERS_WILKINS,
			List.of(Color.BLACK, Color.TAN, Color.ROYAL_BURGUNDY, Color.DARK_FOREST),
			500L,
			"2022-10-11",
			List.of(
				"35ca4633-428e-4b70-a0a6-1d78c0958e39_01.jpg",
				"3bc3714f-4828-42a9-813d-3242d4ee68bc_02.jpg",
				"0425ba7c-f46a-456e-afd0-740437d3d75d_03.jpg",
				"7c4e9794-3f2a-4ca2-af17-f571e04f0c45_04.jpg",
				"fbec1ec5-ce62-4e72-af67-9e77bcf46ffa_05.jpg",
				"c2962028-c631-4dfc-ae8d-78d5a9da1ac4_06.jpg",
				"6d01fa3b-98ed-4d34-a883-3733e1ceb4cb_07.jpg",
				"8af9aec9-8d0c-4ad3-af43-2d0c835cdd2b_08.jpg"
			),
			List.of(
				"41697052-d3d9-4230-ae09-36c12364491f_01.jpg",
				"0606391c-ef94-4bf0-a58f-6b751d74be35_02.jpg",
				"c79ede0e-3edf-4158-b9bb-8cb4248ac530_03.jpg",
				"6b5abe39-01a0-4c66-900c-21a01cb86c7e_04.jpg",
				"d04c20ce-0b5b-466d-9c20-d22adf4cd6bb_05.jpg",
				"3755603c-b0e8-438c-87b7-eaeadb350459_06.jpg"
			)
		);

		saveProduct(
			"MONITOR III A.N.C.",
			Brand.MARSHALL,
			List.of(Color.BLACK),
			500L,
			"2024-11-14",
			List.of(
				"d3bd1665-c514-475e-8010-e3fce010ed64_01.jpg",
				"8582d8c4-571e-4c8a-a93e-cd5051f15b14_02.jpg",
				"6bf78dc1-1933-4679-95fe-0a1448f18062_03.jpg",
				"3ed05c78-59b7-4746-bf33-d7f0c55a9ff3_04.jpg",
				"07c7b681-d71a-4150-90ca-69bf486df9d5_05.jpg",
				"ae754941-6f79-4193-b848-32d7fb687bde_06.jpg",
				"afaba3fe-9c30-485d-9334-3da3872ad3c2_07.jpg",
				"52da88c2-dbe3-4c33-a1c1-5d621b9b56ca_08.jpg",
				"da073155-fe1e-465b-b925-56b32724a5a6_09.jpg",
				"51e80af6-404e-4822-9e98-d5e52dd5caa5_10.jpg"
			),
			List.of(
				"a037357a-c184-4cdb-8c5a-0cb4518b2c43_01.jpg",
				"7293f895-d4c2-403b-8ad8-78609083e4a0_02.jpg",
				"5aff0fff-676d-48e0-892a-22fbf2aaa975_03.jpg",
				"275bc86a-161f-4ece-98af-6f0e8a58748e_04.jpg",
				"9734481b-c1c7-47c5-ad3f-75a7a6dbe258_05.jpg",
				"6838534b-d517-4e21-8f76-66b069e5e907_06.jpg",
				"8d0578f0-1afd-408e-960c-6e27be5c5f6d_07.jpg",
				"2ce5017e-a637-4a2c-9e75-5e58a52894bd_08.jpg",
				"8c444e36-eb9d-4e66-8d77-a5ae31a5f7f9_09.jpg",
				"1b9e8c89-08c2-4782-a8f6-04a53838e947_10.jpg",
				"cf266ee7-8663-4660-83dd-83afb8ec83c7_11.jpg",
				"2619d141-7a16-4fdd-a025-43fd5258a5be_12.jpg",
				"bf85ad36-deb8-4422-a5c1-eee0099e191b_13.jpg"
			)
		);

		saveProduct(
			"Dyson Ontrac",
			Brand.DYSON,
			List.of(Color.BLACK_NICKEL, Color.CERAMIC_CINNABAR, Color.COPPER, Color.ALUMINUM),
			500L,
			"2024-09-03",
			List.of(
				"429e4828-abd9-4143-b559-f8021c87de0a_01.jpg",
				"ab6c301b-139f-447c-b577-c50dcb9f06cb_02.jpg",
				"902d4dbb-6e14-47ea-ada2-daac3b27437d_03.jpg",
				"a5ce67bd-3500-455b-9c15-bb8b7b660f46_04.jpg",
				"7e266e6e-956b-4de2-a9b5-41892e153784_05.jpg",
				"bf2f1157-acaa-43ed-a9d7-52fc261139d3_06.jpg",
				"d599e409-a2e5-4efe-984d-2b96a2d4f8e6_07.jpg",
				"889ff8e4-6439-4ce1-88d5-c3e5e50f834f_08.jpg",
				"b05aacbf-cae7-47ce-ad0c-3088b7aa7370_09.jpg",
				"d23612ab-4b69-4041-a582-51d386722bd2_10.jpg"
			),
			List.of(
				"4c8397ad-3c69-4b91-b43d-8e24ab2d7737_01.jpg",
				"a77bf9d4-4997-4cd5-aa6f-d50ca58eb098_02.jpg",
				"4f599292-0806-4400-9578-bf93153325c8_03.jpg",
				"07239005-d697-4d6e-b9ea-9a3ae72586ff_04.jpg",
				"02a19a4c-736a-4cfd-a27f-19b49dc9f116_05.jpg",
				"d86673ef-0a64-475d-b268-a97be1c017b2_06.jpg",
				"43c881d1-b491-4d82-99fb-1def9db64113_07.jpg",
				"2375e473-b2de-4429-a7ac-d189cc1b70f1_08.jpg",
				"35392ff6-bc2b-4cd7-b26c-f2a911428b49_09.jpg"
			)
		);

		saveProduct(
			"JBL TOUR ONE M2",
			Brand.JBL,
			List.of(Color.BLACK, Color.CHAMPAGNE),
			500L,
			"2023-06-29",
			List.of(
				"359fad3a-f593-4c43-9413-9e487e8a6674_01.jpg",
				"7ea3e6b1-de9c-4d1c-b293-193dd8eefd32_02.jpg",
				"013da785-b4c4-435d-ae22-449dc91f8776_03.jpg",
				"e9951759-b4c9-400c-a10e-1517edf6cf96_04.jpg",
				"dde643d3-cd42-4e39-a47b-1a7e8a23f997_05.jpg",
				"079e79d7-f390-4f27-adbe-99e7a680a6d6_06.jpg",
				"b1da29bc-9d48-4269-b08a-1af2641265cf_07.jpg",
				"5aa8b0c8-9c86-4ae3-96d5-8da9da49bbc9_08.jpg"
			),
			List.of(
				"eeadcd42-067e-4de7-ae5d-f5db879da9a2_01.jpg",
				"6992a814-baa1-4fa1-8133-5f81ba471eea_02.jpg",
				"ade36bef-ace5-4c75-8440-d503fd1b6049_03.jpg",
				"092cb8e6-9567-436e-a121-57f9991be709_04.jpg",
				"742fd8aa-d4e9-472e-9134-3e710763c912_05.jpg",
				"f3ba7d55-0433-44ac-958c-1d632986258e_06.jpg",
				"8ae9eb36-cd1e-48a0-84b8-5b1c4d990dec_07.jpg",
				"24d37d8b-abbe-445b-bf4b-8010162cf4d9_08.jpg",
				"13b8a1fc-b3d3-4c52-81dc-d6a4ee7aa7ef_09.jpg"
			)
		);

		saveProduct(
			"Nothing headphone (1)",
			Brand.NOTHING,
			List.of(Color.BLACK, Color.WHITE),
			500L,
			"2025-07-15",
			List.of(
				"a13f828b-bfbe-4d5b-8b02-210a5a7aa69e_01.jpg",
				"7c7765ab-8bf6-4184-b290-1a2fccce93e1_02.jpg",
				"d5fccb1e-d3e5-4624-b5cd-c5cd0d304a33_03.jpg",
				"90d4be2a-f715-46c9-a224-fb5399b6f6b6_04.jpg",
				"920aba0a-b7f8-4124-9454-2d3816a3c0ea_05.jpg",
				"a47a63ec-5613-4c6c-8f0b-103131f85a46_06.jpg"
			),
			List.of(
				"8beba15a-7e31-4515-b755-7d320d83f3b0_01.jpg",
				"c929c7d4-cc11-462f-8bdb-444fea34b31f_02.jpg",
				"8c6dfb7e-3560-42dc-98ba-e542dd9e6267_03.jpg",
				"69c6c174-1f77-447e-b47a-bf4388f29c18_04.jpg",
				"1daafad1-c737-4421-b7b4-4801e1649bc5_05.jpg",
				"f619a5ee-baa9-47ce-b81d-9af922b93bf8_06.jpg",
				"a31d5648-b77b-41c3-be30-c87dfac5a44a_07.jpg",
				"b72fc5c6-b9be-4634-beca-930afd1df646_08.jpg",
				"1e1dedb6-a7f6-4c18-9990-d537e92c9fe8_09.jpg",
				"e04445db-c3aa-4a38-b5ca-873dc70aa56c_010.jpg",
				"533746a3-e399-44d6-a06e-1051a737b3f9_011.jpg",
				"d3192dab-527d-4fae-adda-fb24a6318453_012.jpg"
			)
		);
	}

	private void saveProduct(
		String name,
		Brand brand,
		List<Color> colors,
		Long dailyRentalPrice,
		String launchedAt,
		List<String> thumbnailImages,
		List<String> detailImages
	) {
		if (productRepository.existsByName(name)) {
			return;
		}

		var request = new ProductCreateRequest(
			name,
			brand,
			colors,
			dailyRentalPrice,
			launchedAt,
			thumbnailImages,
			detailImages
		);
		productCreateService.createProduct(request);
	}
}