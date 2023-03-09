import java.util.ArrayList;
import java.util.Collection;

//public class Folders {
//    private static Collection<String> folderNames(String xml, char startingLetter) throws Exception {
//
//        public class Folder {
//            private static Collection<String> folderNames(String xml, char startingLetter) throws Exception {
//
//                ArrayList<String> coll = new ArrayList<>();
//                int index;
//                String subString;
//                String[] parts;
//                String requiredString = "<folder name=\"";
//
//                index = xml.indexOf(requiredString);
//                subString = xml.substring(index+requiredString.length());
//                parts = subString.split("\"");
//                if (parts[0].contentEquals(Character.toString(startingLetter))) {
//                    coll.add(parts[0]);
//                }
//
//                while(index >0) {
//
//                    index = xml.indexOf(requiredString, index+1);
//                    if(index >0) {
//                        subString = xml.substring(index);
//
//                        if (subString.split("\"")[1].startsWith(Character.toString(startingLetter))) {
//
//                            coll.add(subString.split("\"")[1]);
//                        }
//                    }
//
//                }
//                return coll;
//
//            }
//
//            public static void main(String[] args) throws Exception {
//                String xml =
//                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
//                                "<folder name=\"c\">" +
//                                "<folder name=\"program files\">" +
//                                "<folder name=\"uninstall information\" />" +
//                                "</folder>" +
//                                "<folder name=\"users\" />" +
//                                "</folder>";
//
//                Collection<String> names = folderNames(xml, 'u');
//                for(String name: names)
//                    System.out.println(name);
//            }
//        }
//}