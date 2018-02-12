package com.itslegit.niroigensuntharam.hivelabs;

//public class GetApplicationInfoAsync extends AsyncTask<Void, Void, Void> {
//    private Context mContext;
//
//    GetApplicationInfoAsync(Context context){
//        mContext = context;
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//
//        try{
//
//            MainActivity.Applications.clear();
//
//            Document doc1 = Jsoup.connect
//                    ("https://aits.encs.concordia.ca/services/software-windows-public-labs").get();
//
//            Element table = doc1.select("table").get(0);
//            Elements rows = table.select("tr");
//
//            for (int i = 0; i < rows.size(); i++)
//            {
//                Element row = rows.get(i);
//                Elements cols = row.select("td");
//
//                String applicationName = cols.select("td").get(0).text();
//
//                String[] rooms = cols.select("td").get(1).text().split(",");
//
//                Application application = new Application();
//
//                application.setApplication(applicationName);
//
//                for (String room: rooms) {
//
//                    if (rooms.length == 0) {
//
//                        break;
//                    } else if (room.contains("H")){
//                        application.addRoom(room);
//                    }
//
//                }
//
//                MainActivity.Applications.add(application);
//            }
//        }
//        catch (Exception ex) {
//            // Print failure
//        }
//        finally {
//            saveApplications();
//        }
//
//        return null;
//    }
//
//    private void saveApplications() {
//
//        List<Application> apps = MainActivity.Applications;
//
//        mAppRef.setValue(apps);
//    }
//}
