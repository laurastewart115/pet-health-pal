package com.example.pet_health_pal.DatabaseHandler

import android.content.Context
import android.os.AsyncTask
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pet_health_pal.DatabaseEntities.PetBreed
import com.example.pet_health_pal.DatabaseEntities.PetType
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.message.BasicNameValuePair
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class PetFinderApiHandler(context: Context, asyncResponse: AsyncResponse): AsyncTask<Object, Object, Boolean>() {

    private val context: Context
    private val asyncResponse :AsyncResponse
    private val GET_BEARER_TOKEN_REQUEST_SCOPE = "https://api.petfinder.com/v2/oauth2/token"
    private val GET_ANIMAL_TYPE_REQUEST_SCOPE = "https://api.petfinder.com/v2/types"
    private val GET_ANIMAL_BREED_REQUEST_SCOPE = "https://api.petfinder.com/v2/types/%s/breeds"
    private val SECRET = "bZY05P72dNYJ0tZyIAaXkp9JFajZGUjigzMdraVo"
    private val API_KEY ="vrMCpBq5DDZp13HNMZAfieAlZfHIGh6r1WoL2yUMx0QVDOMIoh"
    private var db = PetDatabaseHandler(context)

    init {
        this.context = context
        this.asyncResponse = asyncResponse;
    }
    private fun getBreedLink(petTypeName: String): String{
        return String.format(GET_ANIMAL_BREED_REQUEST_SCOPE,petTypeName)
    }
    private fun GetBearerToken(asyncResponse: AsyncResponse) {
           try {
               val rq: RequestQueue = Volley.newRequestQueue(context)
               val request = object:StringRequest(Method.POST, GET_BEARER_TOKEN_REQUEST_SCOPE,Response.Listener {response ->
                    asyncResponse.onResponseRecieved(JSONObject(response).get("access_token").toString(),null)
               }, Response.ErrorListener {
                    asyncResponse.onErrorListener("Error Getting Bearer Token")
               }){
                   override fun getParams(): MutableMap<String, String> {
                       var params = HashMap<String,String>()
                       params.put("grant_type", "client_credentials")
                       params.put("client_id", API_KEY)
                       params.put("client_secret", SECRET)

                       return params
                   }
               }
               rq.add(request)
           } catch (ex: java.lang.Exception) {
               System.out.println(ex)
           }
    }

    private fun UploadAnimalTypesToDatabase(token: String ,asyncResponse: AsyncResponse){
        try {
            val rq: RequestQueue = Volley.newRequestQueue(context)
            val request = object:StringRequest(Method.GET, GET_ANIMAL_TYPE_REQUEST_SCOPE,Response.Listener {response ->
                var jsonObject = JSONObject(response)
                var AnimalTypeArray = jsonObject.getJSONArray("types")
                db.deleteAllFromPetType()
                for(x in 0 until AnimalTypeArray.length()){
                    var CoatsList = ArrayList<String>()
                    var ColorsList = ArrayList<String>()
                    var GendersList = ArrayList<String>()

                    var TypeObject = AnimalTypeArray.getJSONObject(x)
                    var TypeName = TypeObject.getString("name")
                    var Coats = TypeObject.getJSONArray("coats")
                        for(x1 in 0 until Coats.length()){
                            CoatsList.add(Coats.getString(x1))
                        }
                    var Colors = TypeObject.getJSONArray("colors")
                    for(x2 in 0 until Colors.length()){
                        ColorsList.add(Colors.getString(x2))
                    }
                    var Genders = TypeObject.getJSONArray("genders")
                    for(x3 in 0 until Genders.length()){
                        GendersList.add(Genders.getString(x3))
                    }

                    var petType = PetType(1,TypeName,CoatsList,ColorsList,GendersList)
                    db.addPetType(petType)
                }

                asyncResponse.onResponseRecieved(token,true)
            }, Response.ErrorListener {
                asyncResponse.onErrorListener("Error getting types of Animals")
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    var headers = HashMap<String,String>()
                    headers.put("Authorization", "Bearer $token")
                    return headers
                }
            }
            rq.add(request)
        } catch (ex: java.lang.Exception) {
            System.out.println(ex)
        }
    }

    private fun UploadBreeds(petTypeList: List<PetType>,index: Int, token: String){
        if(index < petTypeList.size){
            try {
                val GET_ANIMAL_BREED = getBreedLink(petTypeList.get(index).TypeName)
                val rq: RequestQueue = Volley.newRequestQueue(context)
                val request = object:StringRequest(Method.GET, GET_ANIMAL_BREED,Response.Listener {response ->
                    val jsonObject = JSONObject(response)
                    val petBreedjsonArray = jsonObject.getJSONArray("breeds")
                    val petBreedList = ArrayList<String>()
                    for(x in 0 until petBreedjsonArray.length()){
                        val AnimalPetBreed = petBreedjsonArray.getJSONObject(x)
                        val BreedName = AnimalPetBreed.get("name").toString()
                        petBreedList.add(BreedName)
                    }
                    val petBreed = PetBreed(1,petTypeList.get(index).TypeName,petBreedList)
                    db.addPetBreed(petBreed)
                    UploadBreeds(petTypeList,index+1,token)
                }, Response.ErrorListener {
                    asyncResponse.onErrorListener(it.toString())
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        var headers = HashMap<String,String>()
                        headers.put("Authorization", "Bearer $token")
                        return headers
                    }
                }
                rq.add(request)
            } catch (ex: java.lang.Exception) {
                System.out.println(ex)
            }
            return
        }
        return
    }

    override fun doInBackground(vararg p0: Object?): Boolean? {
        var InformationUploaded = false
        //Getting Bearer Token
        GetBearerToken(object : AsyncResponse{
            override fun onResponseRecieved(s: String, b: Boolean?) {
                //Getting Animal types and pushing them to the databas
                UploadAnimalTypesToDatabase(s,object : AsyncResponse{
                    override fun onResponseRecieved(s: String, b: Boolean?) {
                        if(b != null){
                            db.deleteAllFromPetBreeds()
                           val petTypeList = db.viewPetTypes()
                           UploadBreeds(petTypeList,0,s)
                        }
                    }
                    override fun onErrorListener(s: String) {
                        asyncResponse.onErrorListener(s)
                    }
                })
            }
            override fun onErrorListener(s: String) {
                asyncResponse.onErrorListener(s)
            }
        })
        return InformationUploaded ;
    }

    override fun onPostExecute(result: Boolean) {
        asyncResponse.onResponseRecieved("",result)
    }
}