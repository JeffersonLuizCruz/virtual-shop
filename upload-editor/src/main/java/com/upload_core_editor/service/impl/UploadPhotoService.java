package com.upload_core_editor.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class UploadPhotoService {

    private final Path directoryAbsolutePath;
    private final Path directoryPhotoOriginal;
    private final Path directoryPhotoMarca;

    private final String marcaDaguaTexto = "SUA MARCA D'ÁGUA"; // Texto da marca d'água

    public UploadPhotoService(FileStoragePropertiesServiceImpl fileStorageProperties) {
        this.directoryAbsolutePath = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        this.directoryPhotoOriginal = Paths.get(fileStorageProperties.getUploadDirOriginal()).normalize();
        this.directoryPhotoMarca = Paths.get(fileStorageProperties.getUploadDirMarca()).normalize();
    }

    public List<String> uploadPhoto(MultipartFile[] files) {
        List<String> resposta = new ArrayList<>();

        try {

            createDirecotyCaseNoExist();

            for (MultipartFile file : files) {
                // Salvar a foto original
                String nomeArquivoOriginal = saveFiles(file);

                // Gerar e salvar a versão com marca d'água
                String nomeArquivoComMarcaDagua = aplicarMarcaDagua(file);

                resposta.add("Original: " + nomeArquivoOriginal + ", Com marca d'água: " + nomeArquivoComMarcaDagua);
            }
        } catch (IOException e) {
            e.printStackTrace();

            //toDO
        }

        return resposta;
    }

    private void createDirecotyCaseNoExist() throws IOException {
        Path dirPhotoOriginal = Paths.get(this.directoryAbsolutePath.toString(), this.directoryPhotoOriginal.toString());
        Path dirPhotoMarca = Paths.get(this.directoryAbsolutePath.toString(), this.directoryPhotoMarca.toString());

        if (!Files.exists(dirPhotoOriginal)) {
            Files.createDirectories(dirPhotoOriginal);  // Cria o diretório se ele não existir
        }

        if (!Files.exists(dirPhotoMarca)) {
            Files.createDirectories(dirPhotoMarca);  // Cria o diretório se ele não existir
        }
    }

    // Método para salvar a foto original
    private String saveFiles(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        System.out.println(fileName);
        Path destination = Paths
                .get(this.directoryAbsolutePath.toString(), this.directoryPhotoOriginal.toString())
                .resolve(fileName);

        file.transferTo(destination);

        return fileName;
    }

    private String aplicarMarcaDagua(MultipartFile file) throws IOException {
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
        String fileName = "marca_" + file.getOriginalFilename();
        Path destination = Paths.get(this.directoryAbsolutePath.toString(), this.directoryPhotoMarca.toString()).resolve(fileName);

      //  File destino = new File(diretorio + nomeArquivo);

        // Salvar a imagem com marca d'água
        ImageIO.write(imagemComMarcaDagua, "jpg", destination.toFile());

        return fileName;
    }
}
