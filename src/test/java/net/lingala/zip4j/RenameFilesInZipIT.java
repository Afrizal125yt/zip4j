package net.lingala.zip4j;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.testutils.TestUtils;
import net.lingala.zip4j.testutils.ZipFileVerifier;
import net.lingala.zip4j.util.InternalZipConstants;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.lingala.zip4j.testutils.TestUtils.getTestFileFromResources;
import static org.assertj.core.api.Assertions.assertThat;

public class RenameFilesInZipIT extends AbstractIT {

  @Test
  public void testRenameWithFileHeaderRenamesSuccessfully() throws IOException {
    ZipFile zipFile = createDefaultZipFile();

    zipFile.renameFile(zipFile.getFileHeader("sample.pdf"), "changed.pdf");

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, PASSWORD, outputFolder, 3, false);
    verifyFileNamesChanged(zipFile, Collections.singletonMap("sample.pdf", "changed.pdf"));
  }

  @Test
  public void testRenameWithFileHeaderRenamesAFolderSuccessfully() throws IOException {
    ZipFile zipFile = createZipFileWithFolder();

    zipFile.renameFile(zipFile.getFileHeader("test-files/sample_directory/"), "test-files/new_directory_name");

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, PASSWORD, outputFolder, 13, false);
    verifyFileNamesChanged(zipFile, Collections.singletonMap("test-files/sample_directory/", "test-files/new_directory_name/"));
  }

  @Test
  public void testRenameWithFileNameRenamesSuccessfully() throws IOException {
    ZipFile zipFile = createDefaultZipFile();

    zipFile.renameFile("sample_text_large.txt", "changed.txt");

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, PASSWORD, outputFolder, 3, false);
    verifyFileNamesChanged(zipFile, Collections.singletonMap("sample_text_large.txt", "changed.txt"));
  }

  @Test
  public void testRenameWithFileNameRenamesAFolderSuccessfully() throws IOException {
    ZipFile zipFile = createZipFileWithFolder();

    zipFile.renameFile(zipFile.getFileHeader("test-files/sample_directory/"), "test-files/가나다");

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, PASSWORD, outputFolder, 13, false);
    verifyFileNamesChanged(zipFile, Collections.singletonMap("test-files/sample_directory/", "test-files/가나다/"));
  }

  @Test
  public void testRenameWithMapWhenNoEntriesExistWithThatNameDoesNotChangeZipFile() throws IOException {
    ZipFile zipFile = createDefaultZipFile();
    Map<String, String> fileNamesMap = new HashMap<>();
    fileNamesMap.put("does_not_exist_1", "does_not_matter_1");
    fileNamesMap.put("does_not_exist_2", "does_not_matter_2");

    zipFile.renameFiles(fileNamesMap);

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, outputFolder, 3);
    verifyFileHeadersDoesNotExist(zipFile, Arrays.asList("does_not_matter_1", "does_not_matter_2"));
  }

  @Test
  public void testRenameWithMapSingleEntry() throws IOException {
    ZipFile zipFile = createDefaultZipFile();
    Map<String, String> fileNamesMap = new HashMap<>();
    fileNamesMap.put("sample_text_large.txt", "new-name.txt");

    zipFile.renameFiles(fileNamesMap);

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, PASSWORD, outputFolder, 3, false);
    verifyFileNamesChanged(zipFile, fileNamesMap);
  }

  @Test
  public void testRenameWithMapForAllEntriesInZip() throws IOException {
    ZipFile zipFile = createDefaultZipFile();
    Map<String, String> fileNamesMap = new HashMap<>();
    fileNamesMap.put("sample_text1.txt", "new-sample_text1.txt");
    fileNamesMap.put("sample_text_large.txt", "new-sample_text_large.txt");
    fileNamesMap.put("sample.pdf", "new-sample.pdf");

    zipFile.renameFiles(fileNamesMap);

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, PASSWORD, outputFolder, 3, false);
    verifyFileNamesChanged(zipFile, fileNamesMap);
  }

  @Test
  public void testRenameWithMapRenamesRootFolder() throws IOException {
    ZipFile zipFile = createZipFileWithFolder();
    Map<String, String> fileNamesMap = new HashMap<>();
    fileNamesMap.put("test-files/", "new-test-files/");

    zipFile.renameFiles(fileNamesMap);

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, PASSWORD, outputFolder, 13, false);
    verifyFileNamesChanged(zipFile, fileNamesMap);
  }

  @Test
  public void testRenameWithMapRenamesSubFolder() throws IOException {
    ZipFile zipFile = createZipFileWithFolder();
    Map<String, String> fileNamesMap = new HashMap<>();
    fileNamesMap.put("test-files/sample_directory/", "new-test-files/new_sample_directory/");

    zipFile.renameFiles(fileNamesMap);

    zipFile.extractAll(outputFolder.getPath());
    verifyFileNamesChanged(zipFile, fileNamesMap);
  }

  @Test
  public void testRenameWithMapForStoreCompressionMethod() throws IOException {
    ZipFile zipFile = createZipFile(CompressionMethod.STORE, false, null);
    Map<String, String> fileNamesMap = new HashMap<>();
    fileNamesMap.put("sample_text1.txt", "new-sample_text1.txt");
    fileNamesMap.put("sample_text_large.txt", "new-sample_text_large.txt");
    fileNamesMap.put("sample.pdf", "new-sample.pdf");

    zipFile.renameFiles(fileNamesMap);

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, PASSWORD, outputFolder, 3, false);
    verifyFileNamesChanged(zipFile, fileNamesMap);
  }

  @Test
  public void testRenameWithMapWithAesEncryption() throws IOException {
    ZipFile zipFile = createZipFile(CompressionMethod.DEFLATE, true, EncryptionMethod.AES);
    Map<String, String> fileNamesMap = new HashMap<>();
    fileNamesMap.put("sample_text1.txt", "new-sample_text1.txt");
    fileNamesMap.put("sample_text_large.txt", "new-sample_text_large.txt");
    fileNamesMap.put("sample.pdf", "new-sample.pdf");

    zipFile.renameFiles(fileNamesMap);

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, PASSWORD, outputFolder, 3, false);
    verifyFileNamesChanged(zipFile, fileNamesMap);
  }

  @Test
  public void testRenameWithMapWithZipStandardEncryption() throws IOException {
    ZipFile zipFile = createZipFile(CompressionMethod.DEFLATE, true, EncryptionMethod.ZIP_STANDARD);
    Map<String, String> fileNamesMap = new HashMap<>();
    fileNamesMap.put("sample_text1.txt", "new-sample_text1.txt");
    fileNamesMap.put("sample_text_large.txt", "new-sample_text_large.txt");
    fileNamesMap.put("sample.pdf", "new-sample.pdf");

    zipFile.renameFiles(fileNamesMap);

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, PASSWORD, outputFolder, 3, false);
    verifyFileNamesChanged(zipFile, fileNamesMap);
  }

  @Test
  public void testRenameWithMapWithAESEncryptionAndUtf8FileName() throws IOException {
    ZipFile zipFile = createZipFileWithFolder(CompressionMethod.DEFLATE, true, EncryptionMethod.AES);
    Map<String, String> fileNamesMap = new HashMap<>();
    fileNamesMap.put("test-files/가나다.abc", "test-files/üßööß.abc");
    fileNamesMap.put("test-files/öüäöäö/", "test-files/가나다/");

    zipFile.renameFiles(fileNamesMap);

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, PASSWORD, outputFolder, 13, false);
    verifyFileNamesChanged(zipFile, fileNamesMap, false);
  }

  @Test
  public void testRenameWithMapProgressMonitor() throws IOException, InterruptedException {
    ZipFile zipFile = createZipFileWithFolder(CompressionMethod.DEFLATE, true, EncryptionMethod.AES);
    Map<String, String> fileNamesMap = new HashMap<>();
    fileNamesMap.put("test-files/가나다.abc", "test-files/üßööß.abc");
    fileNamesMap.put("test-files/öüäöäö/", "test-files/가나다/");

    zipFile.setRunInThread(true);
    ProgressMonitor progressMonitor = zipFile.getProgressMonitor();

    zipFile.renameFiles(fileNamesMap);

    boolean percentBetweenZeroAndHundred = false;
    boolean fileNameSet = true;
    while (ProgressMonitor.State.BUSY.equals(progressMonitor.getState())) {
      int percentDone = progressMonitor.getPercentDone();
      String fileName = progressMonitor.getFileName();

      if (percentDone > 0 && percentDone < 100) {
        percentBetweenZeroAndHundred = true;
      }

      if (fileName != null) {
        fileNameSet = true;
      }
    }

    assertThat(progressMonitor.getResult()).isEqualTo(ProgressMonitor.Result.SUCCESS);
    assertThat(progressMonitor.getState().equals(ProgressMonitor.State.READY));
    assertThat(progressMonitor.getException()).isNull();
    assertThat(percentBetweenZeroAndHundred).isTrue();
    assertThat(fileNameSet).isTrue();

    ZipFileVerifier.verifyZipFileByExtractingAllFiles(generatedZipFile, PASSWORD, outputFolder, 13, false);
    verifyFileNamesChanged(zipFile, fileNamesMap, false);
  }

  private void verifyFileNamesChanged(ZipFile zipFile, Map<String, String> fileNamesMap) throws IOException {
    verifyFileNamesChanged(zipFile, fileNamesMap, true);
  }

  private void verifyFileNamesChanged(ZipFile zipFile, Map<String, String> fileNamesMap, boolean verifyFileContent) throws IOException {
    verifyFileHeadersDoesNotExist(zipFile, fileNamesMap.keySet());
    verifyFileHeadersExist(zipFile, fileNamesMap.values());

    for (Map.Entry<String, String> changedFileNameEntry : fileNamesMap.entrySet()) {
      if (changedFileNameEntry.getValue().endsWith(InternalZipConstants.ZIP_FILE_SEPARATOR)) {
        List<FileHeader> allFileHeaders = zipFile.getFileHeaders();
        for (FileHeader fileHeader : allFileHeaders) {
          assertThat(fileHeader.getFileName()).doesNotStartWith(changedFileNameEntry.getKey());
        }
      } else {
        if (verifyFileContent) {
          verifyContentOfChangedFile(changedFileNameEntry.getKey(), changedFileNameEntry.getValue());
        }
      }
    }
  }

  private void verifyFileHeadersExist(ZipFile zipFile, Collection<String> fileNamesToVerify) throws IOException {
    for (String fileNameToVerify : fileNamesToVerify) {
      assertThat(zipFile.getFileHeader(fileNameToVerify)).isNotNull();
    }
  }

  private void verifyFileHeadersDoesNotExist(ZipFile zipFile, Collection<String> fileNamesToVerify) throws IOException {
    for (String fileNameToVerify : fileNamesToVerify) {
      assertThat(zipFile.getFileHeader(fileNameToVerify)).isNull();
    }
  }

  private void verifyContentOfChangedFile(String oldFileName, String newFileName) throws IOException {
    File sourceFile = getTestFileFromResources(oldFileName);
    File extractedFile = Paths.get(outputFolder.getAbsolutePath(), newFileName).toFile();
    ZipFileVerifier.verifyFileCrc(sourceFile, extractedFile);
  }

  private ZipFile createDefaultZipFile() throws ZipException {
    return createZipFile(CompressionMethod.DEFLATE, false, null);
  }

  private ZipFile createZipFile(CompressionMethod compressionMethod, boolean encrypt, EncryptionMethod encryptionMethod) throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile, PASSWORD);
    ZipParameters zipParameters = buildZipParameters(compressionMethod, encrypt, encryptionMethod);
    zipFile.addFiles(FILES_TO_ADD, zipParameters);
    return zipFile;
  }

  private ZipFile createZipFileWithFolder() throws ZipException {
    return createZipFileWithFolder(CompressionMethod.DEFLATE, false, null);
  }

  private ZipFile createZipFileWithFolder(CompressionMethod compressionMethod, boolean encrypt, EncryptionMethod encryptionMethod) throws ZipException {
    ZipFile zipFile = new ZipFile(generatedZipFile, PASSWORD);
    ZipParameters zipParameters = buildZipParameters(compressionMethod, encrypt, encryptionMethod);
    zipFile.addFolder(TestUtils.getTestFileFromResources(""), zipParameters);
    return zipFile;
  }

  private ZipParameters buildZipParameters(CompressionMethod compressionMethod, boolean encrypt, EncryptionMethod encryptionMethod) {
    ZipParameters zipParameters = new ZipParameters();
    zipParameters.setEncryptFiles(encrypt);
    zipParameters.setEncryptionMethod(encryptionMethod);
    zipParameters.setCompressionMethod(compressionMethod);
    return zipParameters;
  }

}