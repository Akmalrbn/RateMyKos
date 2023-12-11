//import packages
const express = require('express');
const session = require('express-session');
const bodyParser = require('body-parser');
const notifier = require('node-notifier');
const cors = require('cors');

 
//initialize the app as an express app
const app = express();
const router = express.Router();
const { Client } = require('pg');
const bcrypt = require('bcrypt');

//Insiasi koneksi ke database
const db = new Client({
    user: 'rplsembilan',
    host: 'ep-green-hall-71202148.ap-southeast-1.aws.neon.tech',
    database: 'ratemykos',
    ssl:{
        rejectUnauthorized: false,
        },
    password: 'qTu27CpWyMXh',
    port: 5432,
    idleTimeoutMillis: 100000,
})

//Melakukan koneksi dan menunjukkan indikasi database terhubung
db.connect((err)=>{
    if(err){
        console.log(err)
        return
    }
    console.log('Database berhasil terkoneksi')
})

//middleware (session)
app.use(
  session({
    secret: 'ini contoh secret',
    saveUninitialized: false,
    resave: false
  })
);
app.use(bodyParser.json());
app.use(
  bodyParser.urlencoded({
    extended: true
  })
);
app.use(cors({
  origin: 'http://10.0.2.2:3000',
  credentials: true
}));
var temp;
 
router.post('/login', (req, res) => {
  const { identifier, password } = req.body;
  console.log(req.body);
  const temp = req.session;
  temp.identifier = identifier;
  temp.password = password;

  let query;
  if (identifier && identifier.includes('@')) {
    query = `SELECT * FROM users WHERE email = '${temp.identifier}'`;
  } else {
    query = `SELECT * FROM users WHERE username = '${temp.identifier}'`;
  }

  db.query(query, (err, results) => {
    if (err) {
      console.error(err);
      res.status(500).send("Error logging in.");
    } else {
      if (results.rows.length > 0) {
        const user = results.rows[0];
        bcrypt.compare(temp.password, user.password, (err, isMatch) => {
          if (err) {
            console.error(err);
            res.status(500).send("Error logging in.");
          } else {
            if (isMatch) {
              res.status(200).send(results.rows[0])
            } else {
              res.status(401).send("Invalid password.");
            }
          }
        });
      } else {
        res.status(401).send("Invalid email/username");
      }
    }
  });
});


router.post('/register', (req, res) => {
    const { username, email, password } = req.body;
    temp = req.session;
    temp.username = username;
    temp.password = password;
    temp.email = email;
    console.log(req.body);
    bcrypt.hash(temp.password, 10, (err, hashedPassword) => {
      if (err) {
        notifier.notify('Hash Gagal');
        return;
      }
  
      // Melakukan registrasi user baru ke dalam database
      const query = `INSERT INTO users (email, username, password) VALUES ('${temp.email}', '${temp.username}', '${hashedPassword}');`;
      db.query(query, (err, results) => {
        if (err) {
          console.log(err);
          notifier.notify("Register Gagal");
          return;
        }
        res.send(`Username: ${req.body.username}, Email: ${temp.email} berhasil terdaftar`);
        res.end();
      });
    });
  });

  app.get('/getuser', async (req, res) => {
    const { identifier } = req.body;
    
      try {
        let query;
        if (identifier.includes('@')) {
          query = `SELECT * FROM users WHERE email = '${identifier}'`;
        } else {
          query = `SELECT * FROM users WHERE username = '${identifier}'`;
        }
        const result = await db.query(query);
        res.json(result.rows[0])
        console.log(result.rows[0]);
      } catch (error) {
        console.error('Error retrieving user:', error);
        return res.status(500).json({ error: 'Internal server error' });
      }
  });
  
app.delete('/deleteacc', async (req, res) => {//tambahin delete semua data user
    const {user_id} = req.body
    temp = req.session;
    temp.user_id = user_id;
    try {
      // Delete the user row from the PostgreSQL database based on the user ID
      const query = 'DELETE FROM users WHERE user_id = $1';
      const values = [user_id];

      await db.query(query, values);
  
      res.sendStatus(200); // Send a success status
    } catch (error) {
      console.error('An error occurred while deleting the user:', error);
      res.status(500).json({ error: 'An error occurred while processing the request' });
    }
  });


//   router.post('/addkos', (req, res) => {
//     const { name, location, latitude, longitude, description, facilities } = req.body;
//     temp = req.session;
//     temp.name = name;
//     temp.location = location;
//     temp.latitude = latitude;
//     temp.longitude = longitude;
//     temp.description = description;
//     temp.facilities = facilities;
//     console.log(req.body);

//     // Construct the SQL query with or without the facilities field
//     let query;
//     let params;
//     if (temp.facilities !== null && temp.facilities !== undefined) {
//         query = `INSERT INTO kos (name, location, latitude, longitude, description, facilities) VALUES ($1, $2, $3, $4, $5, $6) RETURNING *;`;
//         params = [temp.name, temp.location, temp.latitude, temp.longitude, temp.description, temp.facilities];
//     } else {
//         query = `INSERT INTO kos (name, location, latitude, longitude, description) VALUES ($1, $2, $3, $4, $5) RETURNING *;`;
//         params = [temp.name, temp.location, temp.latitude, temp.longitude, temp.description];
//     }

//     // Execute the query
//     db.query(query, params, (err, results) => {
//         if (err) {
//             console.log(err);
//             notifier.notify("Register Gagal");
//             res.status(500).json({ error: 'Failed to register kos' });
//             return;
//         }

//         const insertedKos = results.rows[0];
//         res.json({ message: `kos: ${insertedKos.name}, pada lokasi: ${insertedKos.location} berhasil terdaftar`, insertedKos });
//     });
// });

router.post('/addkos', (req, res) => {
  const { name, location, latitude, longitude, description, facilities, kos_type } = req.body;
  const query = `
      INSERT INTO kos (name, location, latitude, longitude, description, facilities, kos_type)
      VALUES ($1, $2, $3, $4, $5, $6::facility[], $7)
      RETURNING *;
  `;

  const values = [name, location, latitude, longitude, description, facilities, kos_type];

  db.query(query, values, (err, result) => {
      if (err) {
          console.error(err);
          res.status(500).json({ error: 'Failed to register kos' });
          return;
      }
      const insertedKos = result.rows[0];
      res.json({ message: `kos: ${insertedKos.name}, pada lokasi: ${insertedKos.location} berhasil terdaftar`, insertedKos });
  });
});


router.post('/addkosnofacilities', (req, res) => {
  const { name, location, latitude, longitude, description } = req.body;
  temp = req.session;
  temp.name = name;
  temp.location = location;ad
  temp.latitude = latitude;
  temp.longitude = longitude;
  temp.description = description;
  console.log(req.body);
  
  const query = `INSERT INTO kos (name, location, latitude, longitude, description) VALUES ('${temp.name}', '${temp.location}', '${temp.latitude}', '${temp.longitude}', '${temp.description}');`;

  // Execute the query
  db.query(query, (err, results) => {
      if (err) {
          console.log(err);
          notifier.notify("Register Gagal");
          return;
      }
      res.json({
        message: `kos: ${temp.name}, pada lokasi: ${temp.location} berhasil terdaftar`
      });
      res.end();
  });
});

router.get('/getkos', async (req, res) => {
  const { name } = req.body;
  try {
    const query = `SELECT * FROM kos WHERE name = $1;`;
    const values = [name];
    const result = await db.query(query, values);
    res.json(result.rows[0])
    console.log(result.rows[0]);
  } catch (error) {
    console.error('Error retrieving user:', error);
    return res.status(500).json({ error: 'Internal server error' });
  }
  });

  // router.get('/getallkos', async (req, res) => {
  // try {
  //   const query = `SELECT * FROM kos;`;
  //   const result = await db.query(query);
  //   res.json(result.rows)
  //   console.log(result.rows);
  // } catch (error) {
  //   console.error('Error retrieving boarding houses:', error);
  //   return res.status(500).json({ error: 'Internal server error' });
  // }
  // });
  
  // router.get('/getallkos', async (req, res) => {
  //   // Assuming facilities is an array of strings in your database
  //   const query = `SELECT * FROM kos;`;
  //   const result = await db.query(query);

  //   const kosList = result.rows.map((kos) => {
  //       return {
  //           ...kos,
  //           facilities: kos.facilities ? kos.facilities.substring(1, kos.facilities.length - 1).split(',') : [],
  //       };
  //   });
  //   res.json(kosList);

  // });

  router.get('/getallkos', async (req, res) => {
    try {
      const query = `
        SELECT
          k.*,
          COALESCE(AVG(r.rating), 0) AS average_rating
        FROM
          kos k
        LEFT JOIN
          ratings r ON k.kos_id = r.kos_id
        GROUP BY
          k.kos_id;
      `;
  
      const result = await db.query(query);
      const kosListWithRatings = result.rows.map((row) => {
        return {
          ...row,
          facilities: row.facilities ? row.facilities.substring(1, row.facilities.length - 1).split(',') : [],
        };
      });
  
      res.json(kosListWithRatings);
    } catch (error) {
      console.error('An error occurred while fetching data from the database:', error);
      res.status(500).json({ error: 'An error occurred while processing the request' });
    }
  });
  
   
router.post('/addcomment', async (req, res) => {
  const { kos_id, user_id, comment} = req.body; // Get the kos ID, user ID, and comment from the request body
  const query = {
    text: 'INSERT INTO comments (user_id, kos_id, comment) VALUES ($1, $2, $3)',
    values: [user_id, kos_id, comment],
  };

  db.query(query, (err, result) => {
    if (err) {
      console.error('Error adding comment:', err);
      res.status(500).json({ error: 'Error adding comment' });
    } else {
      console.log('Comment added successfully');
      res.status(200).json({ message: 'Comment added successfully' });
    }
  });
});

router.get('/getcomment/:kos_id', async (req, res) => {
  const { kos_id } = req.params; // Get the kos_id from the request parameters
  const query = {
    text: `SELECT c.comment_id, c.comment, c.user_id, c.reply_id, c.created_at, u.username
            FROM comments AS c
            JOIN users AS u ON c.user_id = u.user_id
            WHERE kos_id = $1
            ORDER BY created_at ASC;`,
    values: [kos_id],
  };

  try {
    const result = await db.query(query);
    console.log('Comments retrieved successfully');
    res.status(200).json(result.rows);
  } catch (err) {
    console.error('Error retrieving comments:', err);
    res.status(500).json({ error: 'Error retrieving comments' });
  }
});


router.post('/addreply', async (req, res) => {
  const { kos_id, user_id, comment, reply_id} = req.body; // Get the movie ID, user ID, and comment from the request body
  const query = {
    text: 'INSERT INTO comments (comment, user_id, kos_id, reply_id) VALUES ($1, $2, $3, $4)',
    values: [comment, user_id, kos_id, reply_id],
  };

  db.query(query, (err, result) => {
    if (err) {
      console.error('Error adding reply:', err);
      res.status(500).json({ error: 'Error adding reply' });
    } else {
      console.log('Reply added successfully');
      res.status(200).json({ message: 'Reply added successfully' });
    }
  });
});

router.delete('/deletecomment', async (req, res) => {
const { comment_id } = req.body; // Get the movie ID, user ID, and comment from the request body
temp = req.session;
temp.comments_ID = comment_id;
try {
    // Delete the comment from the PostgreSQL database
    const query = 'DELETE FROM comments WHERE comment_id = $1';
    const values = [temp.comments_ID];

    await db.query(query, values);
    res.send(`komen berhasil dihapus`);
} catch (error) {
    console.error('An error occurred while deleting the comment from the database:', error);
    res.status(500).json({ error: 'An error occurred while processing the request' });
}
});


router.get('/getreply/:reply_id', async (req, res) => {
  const { reply_id } = req.params;
  try {
    const query = `
    SELECT c.comment, c.user_id, u.username
    FROM comments AS c
    JOIN users AS u ON c.user_id = u.user_id
    WHERE reply_id = '${reply_id}' ORDER BY created_at ASC`;

    const result = await db.query(query);
    const comments = result.rows;
    res.json(comments); // Send the comments as a JSON response
 
  } catch (error) {
    console.error('Error retrieving replies:', error);
  }
});


// router.get('/getrating/:kos_id', async (req, res) => {
//   const { kos_id } = req.params; 
//   try {
//       // Retrieve the average rating from the PostgreSQL database based on the kos ID
//       const query = 'SELECT AVG(rating) AS average_rating FROM ratings WHERE kos_id = $1';
//       const values = [kos_id];

//       const result = await db.query(query, values);
//       const rating = result.rows[0].average_rating;

//       res.json({ rating }); // Send the average rating as a JSON response
//   } catch (error) {
//       console.error('An error occurred while fetching data from the database:', error);
//       res.status(500).json({ error: 'An error occurred while processing the request' });
//   }
// });

router.get('/getuserrating/:kos_id', async (req, res) => {
  const { kos_id } = req.params; 
  const { user_id } = req.body; 
  try {
      // Retrieve the average rating from the PostgreSQL database based on the kos ID & user_id
      const query = 'SELECT rating FROM ratings WHERE kos_id = $1 and user_id = $2';
      const values = [kos_id, user_id];

      const result = await db.query(query, values);
      const rating = result.rows[0].rating;
      res.json({ rating }); // Send the average rating as a JSON response
  } catch (error) {
      console.error('An error occurred while fetching data from the database:', error);
      res.status(500).json({ error: 'An error occurred while processing the request' });
  }
});

router.post('/addrating', async (req, res) => {
  const { user_id, kos_id, rating } = req.body; 
  try {
    // Check if the rating already exists for the given user_id and kos_id
    const result = await db.query('SELECT * FROM ratings WHERE user_id = $1 AND kos_id = $2', [user_id, kos_id]);

    if (result.rows.length > 0) {
      // If the rating exists, update it
      await db.query('UPDATE ratings SET rating = $1 WHERE user_id = $2 AND kos_id = $3', [rating, user_id, kos_id]);
    } else {
      // If the rating doesn't exist, insert a new row
      await db.query('INSERT INTO ratings (user_id, kos_id, rating) VALUES ($1, $2, $3)', [user_id, kos_id, rating]);
    }

    // Send a JSON response on success
    res.status(200).json({ message: 'Rating stored successfully' });
    console.log('Rating stored successfully');

  } catch (error) {
    console.error('An error occurred while storing the rating:', error);
    res.status(500).json({ success: false, error: 'An error occurred while processing the request' });
  }
});

  
//Router 7: menghapus session
router.post('/logout', (req, res) => {
  // Destroy the session
  req.session.destroy((err) => {
    if (err) {
      console.error('Error logging out:', err);
      res.status(500).send("Error logging out.");
    } else {
      res.status(200).send("Logged out successfully.");
    }
  });
});

// Router 8: Booking room
router.post('/bookroom', async (req, res) => {
  const { user_id, kos_id } = req.body;
 
  try {
   const query = `
     INSERT INTO bookings (user_id, kos_id)
     VALUES ($1, $2)
   `;
 
   const values = [user_id, kos_id];
 
   await db.query(query, values);
 
   res.status(200).send('Room booked successfully');
  } catch (error) {
   console.error('An error occurred while booking the room:', error);
   res.status(500).json({ error: 'An error occurred while processing the request' });
  }
 });
 
 
app.use('/', router);
app.listen(process.env.PORT || 3000, () => {
    console.log(`App Started on PORT ${process.env.PORT || 3000}`);
});

