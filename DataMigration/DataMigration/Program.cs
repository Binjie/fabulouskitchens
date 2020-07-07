using MySql.Data.MySqlClient;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity.Validation;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DataMigration
{
    public static class Program
    {
        static FileStream fs = new FileStream("log" + DateTime.Now.ToString("yyyyMMddhhmmss") + ".txt", FileMode.Create);
        static StreamWriter sw = new StreamWriter(fs);

        static void Main(string[] args)
        {
            string myconn = "Database='fms';Data Source=192.168.1.99;User ID=fms;Password=fabulous;CharSet=utf8;Connection Timeout=6000;Charset=utf8";
            string mysql = "SELECT * from fabulousKitchens.records where fileNumber='M-2599(Arts&Craft room)'";
            MySqlConnection myconnection = new MySqlConnection(myconn);
            myconnection.Open();
            MySqlCommand mycommand = new MySqlCommand(mysql, myconnection);
            mycommand.CommandTimeout = 0;
            MySqlDataAdapter myad = new MySqlDataAdapter(mycommand);
            DataSet dsrecord = new DataSet();
            myad.Fill(dsrecord);
            myconnection.Close();
            using (fmsEntities1 fe = new fmsEntities1())
            {
                foreach (DataRow row in dsrecord.Tables[0].Rows)
                {
                    string fileNumber = row["fileNumber"].ToString();
                    sw.WriteLine(fileNumber);

                    Console.WriteLine(fileNumber);
                    quotation quo = null;
                    quo = fe.quotations.FirstOrDefault<quotation>(c => c.file_number == fileNumber);
                    if (quo == null)
                    {
                        quo = new quotation();
                        FillData(row, quo);
                        fe.quotations.Add(quo);
                    }
                    else
                    {
                        FillData(row, quo);
                    }
                    quo.created = DateTime.Now;
                    quo.create_by = "Data Migration";
                    quo.is_deleted = "N";
                    try
                    {
                        fe.SaveChanges();
                    }
                    catch (DbEntityValidationException ex)
                    {
                        foreach (var err in ex.EntityValidationErrors)
                        {
                            foreach (var errmsg in err.ValidationErrors)
                            {
                                sw.WriteLine(errmsg.ErrorMessage);
                            }
                            err.ValidationErrors.Clear();
                        }
                        sw.WriteLine("Insert fail for DbEntityValidationException." + fileNumber);
                    }

                    sw.WriteLine();
                    sw.Flush();
                }
            }
            sw.Close();
            fs.Close();

        }

        static void FillData(DataRow row, quotation quo)
        {
            string sent = row["sent"].ToString();
            string fileNumberPlus = row["fileNumberPlus"].ToString();
            string concurrency = row["concurrency"].ToString();

            string filename = row["fileNumber"].ToString();
            //string subfile = string.Empty;
            //if (filename.Contains("(") && filename.Contains(")"))
            //{
            //    int st = filename.IndexOf("(");
            //    int ed = filename.LastIndexOf(")");
            //    if (ed > st)
            //    {
            //        subfile = filename.Substring(st + 1, ed - st - 1);
            //        filename = filename.Substring(0, st);
            //    }
            //}
            quo.file_number = filename;
            //quo.sub_file = subfile;
            quo.particulars = row["particulars"].ToString();
            if (row["date"].ToString() != string.Empty)
            {
                quo.quotation_sent = row["date"].ToString().toDate();
            }
            quo.customer_name = row["name"].ToString();
            quo.fax = row["fax"].ToString();
            quo.email = row["email"].ToString();
            quo.customer_record = row["records"].ToString();
            quo.phone = row["phone"].ToString();
            quo.mobile = row["mobile"].ToString();
            quo.job_location = row["location"].ToString();

            string data = row["data"].ToString();
            byte[] byteArray = System.Text.Encoding.UTF8.GetBytes(data);
            object obj = PHPSerializer.UnSerialize(byteArray);
            Hashtable tb = obj as Hashtable;

            if (tb != null)
            {
                quo.stud_height = tb[0].toStr();
                quo.finish_height = tb[1].toStr();
                quo.ic_color = tb[2].toStr();
                quo.ic_edge = tb[3].toStr();
                //quo.extra_notes = tb[4].toStr();
                quo.door_color = tb[5].toStr();
                quo.door_finishing = tb[6].toStr();
                quo.door_edge = tb[7].toStr();
                quo.panel_color = tb[8].toStr();
                quo.panel_finishing = tb[9].toStr();
                quo.panel_edge = tb[10].toStr();
                quo.df_color = tb[11].toStr();
                quo.df_finishing = tb[12].toStr();
                quo.df_edge = tb[13].toStr();
                quo.up_to_celling = tb[14].toStr();
                quo.up_to_bulkhead = tb[118].toStr();
                quo.cornice = tb[16].toStr();
                quo.toekick_colour = tb[15].toStr();

                quo.appliance_type1 = "Oven";
                quo.appliance_model1 = tb[33].toStr();
                quo.appliance_dimension1 = tb[46].toStr();
                quo.appliance_type2 = "Hob";
                quo.appliance_model2 = tb[34].toStr();
                quo.appliance_dimension2 = tb[47].toStr();
                quo.appliance_type3 = "Rangehood";
                quo.appliance_model3 = tb[35].toStr();
                quo.appliance_dimension3 = tb[48].toStr();
                quo.appliance_type4 = "Dishwasher";
                quo.appliance_model4 = tb[36].toStr();
                quo.appliance_dimension4 = tb[49].toStr();
                quo.appliance_type5 = "Microwave";
                quo.appliance_model5 = tb[37].toStr();
                quo.appliance_dimension5 = tb[50].toStr();
                quo.appliance_type6 = "M/W TrimKit";
                quo.appliance_model6 = tb[38].toStr();
                quo.appliance_dimension6 = tb[51].toStr();
                quo.appliance_type7 = "Fridge";
                quo.appliance_model7 = tb[39].toStr();
                quo.appliance_dimension7 = tb[52].toStr();
                quo.appliance_type7 = "Fridge";
                quo.appliance_model7 = tb[39].toStr();
                quo.appliance_dimension7 = tb[52].toStr();
                quo.appliance_type8 = tb[43].toStr();
                quo.appliance_model8 = tb[40].toStr();
                quo.appliance_dimension8 = tb[53].toStr();
                quo.appliance_type9 = tb[44].toStr();
                quo.appliance_model9 = tb[41].toStr();
                quo.appliance_dimension9 = tb[54].toStr();
                quo.appliance_type10 = tb[45].toStr();
                quo.appliance_model10 = tb[42].toStr();
                quo.appliance_dimension10 = tb[55].toStr();

                quo.accessories_type1 = "Sink";
                quo.accessories_model1 = tb[66].toStr();
                quo.accessories_options1 = tb[67].toStr();
                quo.accessories_type2 = "Rubbish Bin";
                quo.accessories_model2 = tb[68].toStr();
                //quo.accessories_options2 = null;
                quo.accessories_type3 = "Runner";
                quo.accessories_model3 = tb[69].toStr();
                quo.accessories_options3 = tb[70].toStr();
                quo.accessories_type4 = "Handles";
                quo.accessories_model4 = tb[71].toStr();
                quo.accessories_options4 = tb[72].toStr();
                quo.accessories_type5 = "Cutlery Tray";
                quo.accessories_model5 = tb[76].toStr();
                quo.accessories_options5 = tb[77].toStr();
                quo.accessories_type6 = "Glass";
                quo.accessories_model6 = tb[73].toStr();
                quo.accessories_options6 = tb[74].toStr();
                quo.accessories_type7 = "Corner System";
                quo.accessories_model7 = tb[75].toStr();
                //quo.accessories_options7 = null;
                quo.accessories_type8 = "Pullout Basket";
                quo.accessories_model8 = tb[78].toStr();
                //quo.accessories_options8 = null;
                quo.accessories_type9 = tb[79].toStr();
                quo.accessories_model9 = tb[80].toStr();
                //quo.accessories_options9 = null;
                quo.accessories_type10 = tb[81].toStr();
                quo.accessories_model10 = tb[82].toStr();
                //quo.accessories_options10 = null;
                quo.accessories_type11 = tb[94].toStr();
                quo.accessories_model11 = tb[95].toStr();
                //quo.accessories_options11 = null;
                quo.accessories_type12 = tb[96].toStr();
                quo.accessories_model12 = tb[97].toStr();
                //quo.accessories_options12 = null;
                quo.accessories_type13 = tb[98].toStr();
                quo.accessories_model13 = tb[99].toStr();
                //quo.accessories_options13= null;
                quo.accessories_type14 = tb[100].toStr();
                quo.accessories_model14 = tb[101].toStr();
                //quo.accessories_options14 = null;

                quo.note_label = tb[106].toStr();
                quo.benchtop_thickness = tb[17].toStr();
                quo.waterfall_quantity = tb[18].toStr();
                quo.waterfall_finish = tb[110].toStr();

                quo.benchtop_range1 = "High Range Granite";
                quo.benchtop_color1 = tb[19].toStr();
                quo.benchtop_price1 = tb[26].toStr().toDecimal();
                quo.benchtop_range2 = "Mid+ Range Granite";
                quo.benchtop_color2 = tb[20].toStr();
                quo.benchtop_price2 = tb[27].toStr().toDecimal();
                quo.benchtop_range3 = "Mid Range Granite";
                quo.benchtop_color3 = tb[21].toStr();
                quo.benchtop_price3 = tb[28].toStr().toDecimal();
                quo.benchtop_range4 = "Low Range Granite";
                quo.benchtop_color4 = tb[22].toStr();
                quo.benchtop_price4 = tb[29].toStr().toDecimal();
                quo.benchtop_range5 = "Standard Range Quartz";
                quo.benchtop_color5 = tb[25].toStr();
                quo.benchtop_price5 = tb[31].toStr().toDecimal();
                quo.benchtop_range6 = "Premium Range Quartz";
                quo.benchtop_color6 = tb[102].toStr();
                quo.benchtop_price6 = tb[103].toStr().toDecimal();
                quo.benchtop_range7 = "Standard Range HPL";
                quo.benchtop_color7 = tb[104].toStr();
                quo.benchtop_price7 = tb[105].toStr().toDecimal();
                quo.benchtop_range8 = tb[107].toStr();
                quo.benchtop_color8 = tb[108].toStr();
                quo.benchtop_price8 = tb[109].toStr().toDecimal();
                quo.benchtop_range9 = tb[23].toStr();
                quo.benchtop_color9 = tb[24].toStr();
                quo.benchtop_price9 = tb[30].toStr().toDecimal();

                quo.additional_notes = tb[83].toStr();
                quo.total_cost = tb[84].toStr().toDecimal();
                quo.C1st_pay_percent = tb[111].toStr().toDecimal();
                quo.C1st_pay_amount_required = tb[114].toStr().toDecimal();
                quo.C1st_pay_amount_paid = tb[88].toStr().toDecimal();
                quo.C1st_pay_date = tb[85].toStr().toDate();
                quo.C2nd_pay_percent = tb[112].toStr().toDecimal();
                quo.C2nd_pay_amount_required = tb[115].toStr().toDecimal();
                quo.C2nd_pay_amount_paid = tb[89].toStr().toDecimal();
                quo.C2nd_pay_date = tb[86].toStr().toDate();
                quo.C3rd_pay_percent = tb[113].toStr().toDecimal();
                quo.C3rd_pay_amount_required = tb[116].toStr().toDecimal();
                quo.C3rd_pay_amount_paid = tb[90].toStr().toDecimal();
                quo.C3rd_pay_date = tb[87].toStr().toDate();

                quo.installation_date = tb[91].toStr().toDate();
                quo.estimated_date = tb[92].toStr().toDate();
                quo.delivery_date = tb[117].toStr().toDate();
                quo.final_note = tb[93].toStr();

                quo.finish_height = tb[1].toStr();
                quo.finish_height = tb[1].toStr();
            }
        }

        public static DateTime? toDate(this string str)
        {
            //str = str.Replace("Jan", "01");
            //str = str.Replace("Feb", "02");
            //str = str.Replace("Mar", "03");
            //str = str.Replace("Apr", "04");
            //str = str.Replace("May", "05");
            //str = str.Replace("Jun", "06");
            //str = str.Replace("Jul", "07");
            //str = str.Replace("Aug", "08");
            //str = str.Replace("Sep", "09");
            //str = str.Replace("Oct", "10");
            //str = str.Replace("Nov", "11");
            //str = str.Replace("Dec", "12");
            if (str != string.Empty)
            {
                try
                {
                    DateTime d = DateTime.Parse(str);
                    return d;
                }
                catch (Exception ex)
                {
                    sw.WriteLine(str + "To Date Fail");
                }
            }
            return null;
        }

        public static decimal? toDecimal(this string str)
        {
            if (str != string.Empty)
            {
                str = str.Replace("$", "");
                try
                {
                    decimal d = decimal.Parse(str);
                    return d;
                }
                catch (Exception ex)
                {
                    sw.WriteLine(str + "To Decimal Fail");
                }
            }
            return null;
        }

        public static string toStr(this object obj)
        {
            if (obj != null)
            {
                return obj.ToString();
            }
            return string.Empty;
        }


    }
}
