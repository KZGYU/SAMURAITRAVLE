package com.example.samuraitravel.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.form.HouseEditForm;
import com.example.samuraitravel.form.HouseRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;

@Service
public class HouseService {
	private final HouseRepository houseRepository; // HouseRepositoryのインスタンスを保持。リポジトリ層へのアクセスを提供する。

	public HouseService(HouseRepository houseRepository) { // コンストラクタでHouseRepositoryのインスタンスをDI（依存性注入）する。
		this.houseRepository = houseRepository;
	}

	@Transactional
	// メソッドがトランザクションの範囲内で実行されることを指定する。データベースの操作が全部成功するか、
	//トランザクションがロールバック（取り消し）され、データベースの変更が破棄される。
	//トランザクション＝データベースの操作をひとまとまりにしたもの
	public void create(HouseRegisterForm houseRegisterForm) { // 住宅登録フォームから受け取った情報を使って、新しい住宅情報を登録するメソッド。
		House house = new House();
		MultipartFile imageFile = houseRegisterForm.getImageFile(); // フォームから受け取った画像ファイル。

		if (!imageFile.isEmpty()) { // 画像ファイルが空でない場合の処理。
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName); // 画像ファイル名をハッシュ化した新しいファイル名に変更。
			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName); // 画像を保存するパスを指定。
			copyImageFile(imageFile, filePath); // 画像ファイルを指定したパスにコピーする。
			house.setImageName(hashedImageName); // ハッシュ化したファイル名を住宅情報に設定。
		}

		// フォームから受け取った情報をHouseエンティティに設定。
		house.setName(houseRegisterForm.getName());
		house.setDescription(houseRegisterForm.getDescription());
		house.setPrice(houseRegisterForm.getPrice());
		house.setCapacity(houseRegisterForm.getCapacity());
		house.setPostalCode(houseRegisterForm.getPostalCode());
		house.setAddress(houseRegisterForm.getAddress());
		house.setPhoneNumber(houseRegisterForm.getPhoneNumber());

		houseRepository.save(house); // 設定したHouseエンティティをデータベースに保存。
	}

	@Transactional

	public void update(HouseEditForm houseEditForm) {

		House house = houseRepository.getReferenceById(houseEditForm.getId());

		MultipartFile imageFile = houseEditForm.getImageFile();

		if (!imageFile.isEmpty()) {

			String imageName = imageFile.getOriginalFilename();

			String hashedImageName = generateNewFileName(imageName);

			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);

			copyImageFile(imageFile, filePath);

			house.setImageName(hashedImageName);

		}

		house.setName(houseEditForm.getName());

		house.setDescription(houseEditForm.getDescription());

		house.setPrice(houseEditForm.getPrice());

		house.setCapacity(houseEditForm.getCapacity());

		house.setPostalCode(houseEditForm.getPostalCode());

		house.setAddress(houseEditForm.getAddress());

		house.setPhoneNumber(houseEditForm.getPhoneNumber());

		houseRepository.save(house);

	}

	// UUIDを使って生成したファイル名を返すメソッド。ファイル名の衝突を避けるために使用する。
	//UUID＝Universally Unique IDentifier（ユニバーサリー・ユニーク・アイデンティファイア）の略。（ほぼ）重複しない一意のIDのこと
	public String generateNewFileName(String fileName) {
		String[] fileNames = fileName.split("\\.");
		for (int i = 0; i < fileNames.length - 1; i++) {
			fileNames[i] = UUID.randomUUID().toString();
		}
		String hashedFileName = String.join(".", fileNames);
		return hashedFileName;
	}

	// 画像ファイルを指定したファイルパスにコピーするメソッド。IOExceptionが発生した場合は、スタックトレースを出力する。
	public void copyImageFile(MultipartFile imageFile, Path filePath) {
		try {
			Files.copy(imageFile.getInputStream(), filePath); // Java NIOのFiles.copyを使用して画像ファイルをコピーする。
		} catch (IOException e) {
			e.printStackTrace(); // 例外が発生した場合は、そのスタックトレースを出力する。
		}
	}
}