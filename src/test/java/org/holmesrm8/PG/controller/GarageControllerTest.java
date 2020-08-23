package org.holmesrm8.PG.controller;

import org.holmesrm8.PG.service.GarageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.io.InputStream;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class GarageControllerTest {

    String url = "/upload-csv-file";

    MockMvc mockMvc;
    private InputStream is;

    @Mock
    GarageService garageService;

    @Spy
    @InjectMocks
    private GarageController controller = new GarageController();
    @Before
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        is = controller.getClass().getClassLoader().getResourceAsStream("HomeworkInputFile.csv");
    }

    @Test (expected = NestedServletException.class)
    public void testUploadCSVFile_emptyCSVFile() throws Exception {
        is = controller.getClass().getClassLoader().getResourceAsStream("empty.csv");

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "file.csv", "multipart/form-data", is);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload(url).file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().is(400)).andReturn();
        Assert.assertEquals(400, result.getResponse().getStatus());
        Assert.assertNotNull(result.getResponse().getContentAsString());
        Assert.assertEquals("CSV File is empty. Please provide file with data", result.getResponse().getContentAsString());
    }

    @Test
    public void testUploadCSVFile() throws Exception {
        when(garageService.saveAllUploads(anyList())).thenReturn(null);

        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "HomeworkInputFile.csv", "multipart/form-data", is);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload(url).file(mockMultipartFile).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
        Assert.assertEquals(200, result.getResponse().getStatus());
        Assert.assertNotNull(result.getResponse().getContentAsString());
        Assert.assertEquals("File upload complete. Results printed in directory under ParkingGarageMonthlyOutput.txt", result.getResponse().getContentAsString());
    }

    @Test(expected = NestedServletException.class)
    public void testUploadCSVFile_nullDataInNotNullField() throws Exception {
        when(garageService.saveAllUploads(anyList())).thenReturn(null);
        is = controller.getClass().getClassLoader().getResourceAsStream("HomeworkInputFile_NullData.csv");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "HomeworkInputFile_NullData.csv", "multipart/form-data", is);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload(url).file(mockMultipartFile).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(400)).andReturn();
       Assert.assertEquals(400, result.getResponse().getStatus());
       Assert.assertTrue(result.getResponse().getContentAsString().contains("Runtime Exception occurred:"));
    }
}
