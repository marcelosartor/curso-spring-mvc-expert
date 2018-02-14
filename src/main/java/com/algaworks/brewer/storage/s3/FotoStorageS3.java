package com.algaworks.brewer.storage.s3;


import static java.nio.file.FileSystems.getDefault;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.storage.FotoStorage;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.sun.mail.iap.ByteArray;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

@Profile("prod")
@Component
public class FotoStorageS3 implements FotoStorage {

	private static final Logger logger = LoggerFactory.getLogger(FotoStorageS3.class);
	
	@Autowired
	private AmazonS3 amazonS3;
	
	private static final String BUCKET = "awbrewer";
	
	@Override
	public String salvar(MultipartFile[] files) {
		String novoNome = null;
		
		if(files != null && files.length>0){
			
			
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename()); 
			
			AccessControlList accessControlList = new AccessControlList();
			accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
			
			try {
			
				enviarFoto(novoNome, arquivo, accessControlList);
				
				enviarThumbnail(novoNome, arquivo, accessControlList);
				
			} catch (IOException e) {
				throw new RuntimeException("Erro ao salvar a foto",e);
			}
		}	
		
			return novoNome;
	}


	private void enviarFoto(String novoNome, MultipartFile arquivo, AccessControlList accessControlList)
			throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(arquivo.getContentType());
		metadata.setContentLength(arquivo.getSize());
		
		amazonS3.putObject(
				new PutObjectRequest(BUCKET, novoNome, arquivo.getInputStream(),metadata)
					.withAccessControlList(accessControlList)				
				);
	}


	private void enviarThumbnail(String novoNome, MultipartFile arquivo, AccessControlList accessControlList)
			throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Thumbnails.of(arquivo.getInputStream()).size(40,68).toOutputStream(os);
		byte[] array = os.toByteArray();
		InputStream is = new ByteArrayInputStream(array);
		
		ObjectMetadata thumbMetadata = new ObjectMetadata();
		thumbMetadata.setContentType(arquivo.getContentType());
		thumbMetadata.setContentLength(array.length);
		
		amazonS3.putObject(
				new PutObjectRequest(BUCKET, THUMBNAIL_PREFIX+novoNome, is ,thumbMetadata)
					.withAccessControlList(accessControlList)				
				);
	}
	
	
	@Override
	public byte[] recuperar(String nomeFoto) {
		InputStream is = amazonS3.getObject(BUCKET, nomeFoto).getObjectContent();
		try {
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			logger.error("Não conseguiu recuperar a foto do S3",e);
		}
		return null;	
	
	}

	@Override
	public byte[] recuperarThumbnail(String fotoCerveja) {
		return recuperar(THUMBNAIL_PREFIX+fotoCerveja); 
	}

	@Override
	public void excluir(String foto) {
		//amazonS3.deleteObjects( new DeleteObjectsRequest(BUCKET).withKey(foto,THUMBNAIL_PREFIX+foto) )   ;
		amazonS3.deleteObjects(new DeleteObjectsRequest(BUCKET).withKeys(foto,THUMBNAIL_PREFIX+foto) );
	}


	@Override
	public String getUrl(String nomeFoto) {
		if(!StringUtils.isEmpty(nomeFoto)) {
		    return "http://localhost:9444/s3/awbrewer/"+nomeFoto + "?noAuth=true";
	
		}
		return null;
	}

}
