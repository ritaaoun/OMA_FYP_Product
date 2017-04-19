package feature_extraction;
import edu.columbia.ccls.madamira.MADAMIRAWrapper;
import edu.columbia.ccls.madamira.configuration.MadamiraInput;
import edu.columbia.ccls.madamira.configuration.MadamiraOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.ExecutionException;

/**
 * Calls MADAMIRA through its API.
 * Modified from MADAMIRA example file: APIExampleUse.
 */
public class Madamira {
    /**
     * MADAMIRA namespace as defined by its XML schema.
     */
    private static final String MADAMIRA_NS = "edu.columbia.ccls.madamira.configuration";

    /**
     * Runs Madamira on input file.
     * @param infilename Location of input file
     * @param outfilename Desired location of output file
     */
    public static void runMadamira(String infilename, String outfilename) {
        final MADAMIRAWrapper wrapper = new MADAMIRAWrapper();
        JAXBContext jc = null;

        try {
            jc = JAXBContext.newInstance(MADAMIRA_NS);
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            InputStream inputStream = new FileInputStream(infilename);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            
            // The structure of the MadamiraInput object is exactly similar to the
            // madamira_input element in the XML
            final MadamiraInput input = (MadamiraInput)unmarshaller.unmarshal(reader);
            {
                int numSents = input.getInDoc().getInSeg().size();
                String outputAnalysis = input.getMadamiraConfiguration().
                        getOverallVars().getOutputAnalyses();
                String outputEncoding = input.getMadamiraConfiguration().
                        getOverallVars().getOutputEncoding();

                System.out.println("processing " + numSents +
                        " sentences for analysis type = " + outputAnalysis +
                        " and output encoding = " + outputEncoding);
            }

            // The structure of the MadamiraOutput object is exactly similar to the
            // madamira_output element in the XML
            final MadamiraOutput output = wrapper.processString(input);

            {
                int numSents = output.getOutDoc().getOutSeg().size();
                System.out.println("processed output contains "+numSents+" sentences...");
            }


            jc.createMarshaller().marshal(output, new File(outfilename));


        } catch (JAXBException ex) {
            System.out.println("Error marshalling or unmarshalling data: "
                    + ex.getMessage());
        } catch (InterruptedException ex) {
            System.out.println("MADAMIRA thread interrupted: "
                    +ex.getMessage());
        } catch (ExecutionException ex) {
            System.out.println("Unable to retrieve result of task. " +
                    "MADAMIRA task may have been aborted: "+ex.getCause());
        }
        catch (Exception e) {
        	e.printStackTrace();
        }

        wrapper.shutdown();
    }
}