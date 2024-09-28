package com.upload_core_editor.service.impl;

import com.upload_core_editor.model.entity.PhotoImage;
import com.upload_core_editor.repository.PhotoImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhotoManagementServiceImpl {

    private final Path directoryAbsolutePath;
    private final Path directoryPhotoOriginal;
    private final Path directoryPhotoMarca;

    @Value("${file.url.photo}")
    private String pathUrl;

    @Autowired private PhotoImageRepository photoImageRepository;

    private final String marcaDaguaTexto = "SUA MARCA D'ÁGUA"; // Texto da marca d'água

    public PhotoManagementServiceImpl(FileStoragePropertiesServiceImpl fileStorageProperties) {
        this.directoryAbsolutePath = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        this.directoryPhotoOriginal = Paths.get(fileStorageProperties.getUploadDirOriginal()).normalize();
        this.directoryPhotoMarca = Paths.get(fileStorageProperties.getUploadDirMarca()).normalize();
    }

    final String nameMockClient = "JoaquimDaSilvaPereira";

    public List<PhotoImage> uploadPhoto(MultipartFile[] files) {
        List<String> photoImages = new ArrayList<>();

        try {

            createDirecotyCaseNoExist();

            for (MultipartFile file : files) {
                String namePhotoOriginal = transferFileToDirectory(file);
                photoImages.add(namePhotoOriginal);

                applyWatermark(file);

            }
        } catch (IOException e) {
            e.printStackTrace();

            //toDO: Implementar exception personalizada.
        }

        List<PhotoImage> photoImageList = photoImages.stream()
                .map(filename -> {
            return PhotoImage.builder()
                    .editor("Hugo Luiz")
                    .client(nameMockClient)
                    .nameFileOriginal(filename)
                    .pathFileOriginal(Paths.get(this.directoryAbsolutePath.toString(), nameMockClient, this.directoryPhotoOriginal.toString()).toString())
                    .urlFileOriginal(pathUrl.concat(Paths.get(this.directoryAbsolutePath.toString(), nameMockClient, this.directoryPhotoOriginal.resolve(filename).toString()).toString()))
                    .nameFileWatermark("watermark_".concat(filename))
                    .pathFileWatermark(Paths.get(this.directoryAbsolutePath.toString(), nameMockClient, this.directoryPhotoMarca.toString()).toString())
                    .urlFileWatermark(pathUrl.concat(Paths.get(this.directoryAbsolutePath.toString(), nameMockClient, this.directoryPhotoMarca.resolve("watermark_".concat(filename)).toString()).toString()))
                    .build();
        }).collect(Collectors.toList());

        return photoImageRepository.saveAll(photoImageList);
    }

    public List<PhotoImage> findAll(){
        return photoImageRepository.findAll();
    }

    private void createDirecotyCaseNoExist() throws IOException {
        Path dirPhotoClient = Paths.get(this.directoryAbsolutePath.toString(), nameMockClient);
        Path dirPhotoOriginal = Paths.get(this.directoryAbsolutePath.toString(), nameMockClient, this.directoryPhotoOriginal.toString());
        Path dirPhotoWatermark = Paths.get(this.directoryAbsolutePath.toString(), nameMockClient, this.directoryPhotoMarca.toString());

        if (!Files.exists(dirPhotoClient)) {
            Files.createDirectories(dirPhotoClient);  // Cria o diretório se ele não existir
        }

        if (!Files.exists(dirPhotoOriginal)) {
            Files.createDirectories(dirPhotoOriginal);  // Cria o diretório se ele não existir
        }

        if (!Files.exists(dirPhotoWatermark)) {
            Files.createDirectories(dirPhotoWatermark);  // Cria o diretório se ele não existir
        }
    }

    // Método para salvar a foto original
    private String transferFileToDirectory(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        Path destination = Paths
                .get(this.directoryAbsolutePath.toString(), nameMockClient, this.directoryPhotoOriginal.toString())
                .resolve(fileName);
        file.transferTo(destination);

        return fileName;
    }

    private void applyWatermark(MultipartFile file) throws IOException {
        BufferedImage imagemOriginal = ImageIO.read(file.getInputStream());

        // Criar uma cópia da imagem original para adicionar a marca d'água
        BufferedImage imagemComMarcaDagua = new BufferedImage(imagemOriginal.getWidth(), imagemOriginal.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) imagemComMarcaDagua.getGraphics();
        g2d.drawImage(imagemOriginal, 0, 0, null);

        // Configurar o estilo da marca d'água
        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f); // Transparência
        g2d.setComposite(alphaChannel);
        g2d.setColor(Color.GRAY); // Cor da marca d'água
        g2d.setFont(new Font("Arial", Font.BOLD, 64)); // Estilo da fonte
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int coordenadaX = (imagemOriginal.getWidth() - fontMetrics.stringWidth(marcaDaguaTexto)) / 2;
        int coordenadaY = imagemOriginal.getHeight() / 2;

        // Adicionar a marca d'água na imagem
        g2d.drawString(marcaDaguaTexto, coordenadaX, coordenadaY);
        g2d.dispose();

        // Definir o nome do arquivo com marca d'água
        String fileName = "watermark_" + file.getOriginalFilename();
        Path destination = Paths.get(this.directoryAbsolutePath.toString(), nameMockClient, this.directoryPhotoMarca.toString()).resolve(fileName);

      //  File destino = new File(diretorio + nomeArquivo);

        // Salvar a imagem com marca d'água
        ImageIO.write(imagemComMarcaDagua, "jpg", destination.toFile());
    }
}
