package com.upload_core_editor.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class UploadPhotoService {

    private final Path fileUploadDir;
    private final Path filePhotoOriginal;
    private final Path filePhtoMarca;

    private final String diretorioOriginal = "caminho/para/original/"; // Defina o diretório para a versão original
    private final String diretorioMarcaDagua = "caminho/para/marcva_dagua/"; // Defina o diretório para a versão com marca d'água
    private final String marcaDaguaTexto = "SUA MARCA D'ÁGUA"; // Texto da marca d'água

    public UploadPhotoService(FileStoragePropertiesServiceImpl fileStorageProperties) {
        this.fileUploadDir = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        this.filePhotoOriginal = Paths.get(fileStorageProperties.getUploadDirOriginal()).toAbsolutePath().normalize();
        this.filePhtoMarca = Paths.get(fileStorageProperties.getUploadDirMarca()).toAbsolutePath().normalize();
    }

    public void uploadPhoto(MultipartFile[] files) {
        List<String> resposta = new ArrayList<>();

        try {
            // Iterar sobre todas as fotos enviadas
            for (MultipartFile file : files) {
                // Salvar a foto original
                String nomeArquivoOriginal = salvarArquivo(file, diretorioOriginal);

                // Gerar e salvar a versão com marca d'água
                String nomeArquivoComMarcaDagua = aplicarMarcaDagua(file, diretorioMarcaDagua);

                // Adicionar a resposta para cada arquivo
                resposta.add("Original: " + nomeArquivoOriginal + ", Com marca d'água: " + nomeArquivoComMarcaDagua);
            }
        } catch (IOException e) {
            e.printStackTrace();

            //todo
        }
    }

    // Método para salvar a foto original
    private String salvarArquivo(MultipartFile file, String diretorio) throws IOException {
        String nomeArquivo = file.getOriginalFilename();
        File destino = new File(diretorio + nomeArquivo);
        file.transferTo(destino);
        return nomeArquivo;
    }

    private String aplicarMarcaDagua(MultipartFile file, String diretorio) throws IOException {
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
        String nomeArquivo = "marca_" + file.getOriginalFilename();
        File destino = new File(diretorio + nomeArquivo);

        // Salvar a imagem com marca d'água
        ImageIO.write(imagemComMarcaDagua, "jpg", destino);

        return nomeArquivo;
    }
}
