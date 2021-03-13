public class assignRoles {
    
}

/*
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MessageListener extends ListenerAdapter
{
    public static void main(String[] args)
    throws LoginException

    String name;
    {
        JDA jda = JDABuilder.createDefault(args[0]).build();
        jda.addEventListeners(new MessageListener());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        //The user that sent the message
        User author = event.getAuthor();
        //The message that was received.                
        Message message = event.getMessage();
        //This is the MessageChannel that the message was sent to.           
        MessageChannel channel = event.getChannel();    
                                                        
        
         //If this is a Webhook message, then there is no Member associated with the User, default to the author for name.
        if (message.isWebhookMessage())
        {
            name = author.getName();               
        }                                           
        else
        { //This will either use the Member's nickname if they have one, otherwise it will default to their username. (User#getName())
            name = member.getEffectiveName();      
        }                                          

        System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
    }
    else if (event.isFromType(ChannelName.general)) 
    {
        //The message was sent in the general channel.
        
        PrivateChannel privateChannel = event.getPrivateChannel();

        System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);
    }

    //List containing the roles
    //Assign number
    //Random number select to assign role

    List<String> givenList = Arrays.asList("banker", "artist", "lawyer", "noble", "spy");
    Random rand = new Random();
    String randomElement = givenList.get(rand.nextInt(givenList.size()));


    if (msg.equals("!join")){
           String role = name.guild.roles.find('name', randomElement);
        }
    }
}
*/



        

        


